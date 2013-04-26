package com.provoz.graph

import it.unimi.dsi.fastutil.ints.{IntSet, IntIterator, IntCollection}
import it.unimi.dsi.fastutil.ints.{Int2ObjectSortedMaps, Int2ObjectSortedMap, Int2ObjectLinkedOpenHashMap}
import it.unimi.dsi.fastutil.ints.{Int2ObjectMaps, Int2ObjectMap, Int2ObjectOpenHashMap}
import it.unimi.dsi.fastutil.Hash

import scala.util.Random

import java.lang.UnsupportedOperationException

import com.provoz.graph.node._

class UnDirectedGraph[Node <: UnDirectedNode](
        protected val initialSize: Option[Int],
        protected val loadFactor: Option[Float]
    ) extends Graph[Node] with UnDirectedGraphs.UnDirectedIterable[Node] {

    protected val nodeMap: Int2ObjectMap[Node] =
        new Int2ObjectOpenHashMap[Node](
            initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
            loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
        )

    private var maxNId: Int = 0
    private var numEdges: Long = 0
    private var startNId: Int = 0

    def maxNodeId: Int = {
        if (nodeMap.size() == 0) -1
        else {
            while(!isNode(maxNId)) maxNId -= 1
            maxNId
        }
    }

    def startNodeId: Int = {
        if (nodeMap.size() == 0) -1
        else {
            while(!isNode(startNId)) startNId += 1
            startNId
        }
    }

    def endNodeId: Int = maxNodeId
    def numOfNodes: Int = nodeMap.size()
    def numOfEdges: Long = numEdges
    def suggestNextNodeId: Int = maxNodeId + 1

    /////////////////////////////////////////////////////////////
    def addOrGetNode(node: Node): Node = {
        nodeMap.put(node.nodeId, node)
        if(node.nodeId > maxNId) maxNId = node.nodeId
        node
    }

    def addOrFailNewNode(node: Node): Node = {
        require(isNode(node.nodeId) == false,
            "Node with node id " + node.nodeId + "already present")
        addOrGetNode(node)
    }

    /////////////////////////////////////////////////////////////
    def addEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode: Node = nodeMap.get(fromNodeId)
        val toNode: Node = nodeMap.get(toNodeId)
        require(fromNode != null || toNode != null,
            "Neither of from or to Node can be non existent id")
        fromNode.nbrNodes.add(toNodeId)
        toNode.nbrNodes.add(fromNodeId)
        numEdges += 1
    }

    def addEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode: Node = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "From node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map( id => {
            val toNode: Node = nodeMap.get(id)
            if (toNode != null){
                toNode.nbrNodes.add(fromNodeId)
                fromNode.nbrNodes.add(id)
                numEdges += 1
            }
        })
    }

    def addEdges(fromNodeId: Int, toNodeIds: IntCollection){
        addEdges(fromNodeId, toNodeIds.toIntArray())
    }
    def addEdges(fromNodeIds: Array[Int], toNodeId: Int){
        addEdges(toNodeId, fromNodeIds)
    }
    def addEdges(fromNodeIds: IntCollection, toNodeId: Int){
        addEdges(toNodeId, fromNodeIds)
    }

    /////////////////////////////////////////////////////////////
    def getNode(nodeId: Int): Node = nodeMap.get(nodeId)
    def getAllNodes(): IntSet = nodeMap.keySet()
    def getRandomNode(rand: Random): Node = {
        var randNode: Node = nodeMap.get(rand.nextInt)
        while(randNode == null) randNode = nodeMap.get(rand.nextInt)
        randNode
    }

    /////////////////////////////////////////////////////////////
    def getNbrsForNode(nodeId: Int): Array[Int] = {
        val node: Node = nodeMap.get(nodeId)
        require(node != null,
            "Node for node id " + nodeId + " doesnot exists")
        node.nbrNodes.toIntArray()
    }

    def getOutNodesForNode(nodeId: Int): Array[Int] = getNbrsForNode(nodeId)
    def getInNodesForNode(nodeId: Int): Array[Int] = getNbrsForNode(nodeId)

    /////////////////////////////////////////////////////////////
    def getNodeIter(): UnDirectedNodeIter[Node] =
        new UnDirectedNodeIter[Node](this.nodeMap)

    def getNodeIterFromNode(nodeId: Int): UnDirectedNodeIter[Node] =
        throw new UnsupportedOperationException(
            "getNodeIterFromNode is not support for default UnDirected Graph, use trait UnDirectedGraph FromAnywhereIterable")

    /////////////////////////////////////////////////////////////
    def removeEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode: Node = nodeMap.get(fromNodeId)
        val toNode: Node = nodeMap.get(toNodeId)
        require(fromNode != null || toNode != null,
            "Neither of from or to Node can be non existent id")
        fromNode.nbrNodes.remove(toNodeId)
        toNode.nbrNodes.remove(fromNodeId)
        numEdges -= 1
    }

    def removeEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode: Node = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "From node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map( id => {
            val toNode: Node = nodeMap.get(id)
            if (toNode != null){
                toNode.nbrNodes.remove(fromNodeId)
                numEdges -= 1
            }
            fromNode.nbrNodes.remove(id)
        })
    }

    def removeEdges(fromNodeId: Int, toNodeIds: IntCollection){
        removeEdges(fromNodeId, toNodeIds.toIntArray())
    }
    def removeEdges(fromNodeIds: Array[Int], toNodeId: Int){
        removeEdges(toNodeId, fromNodeIds)
    }
    def removeEdges(fromNodeIds: IntCollection, toNodeId: Int){
        removeEdges(toNodeId, fromNodeIds)
    }

    /////////////////////////////////////////////////////////////
    def removeNode(nodeId: Int){
        val node: Node = nodeMap.get(nodeId)
        if (node != null){
            val iterator:IntIterator = node.nbrNodes.iterator()
            while(iterator.hasNext()) {
                val nbrNode: Node = nodeMap.get(iterator.nextInt())
                if (nbrNode != null){
                    nbrNode.nbrNodes.remove(nodeId)
                }
                numEdges -= 1
            }
            nodeMap.remove(nodeId)
        }
    }

    def removeNodes(nodeIds: Array[Int]){
        nodeIds.map(id => removeNode(id))
    }

    def removeNodes(nodeIds: IntCollection){
        val it: IntIterator = nodeIds.iterator()
        while(it.hasNext()) removeNode(it.nextInt())
    }

    /////////////////////////////////////////////////////////////
    def isNode(nodeId: Int): Boolean = nodeMap.containsKey(nodeId)

    def isEdge(fromNodeId: Int, toNodeId: Int): Boolean = {
        val fromNode: Node = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "From node with node id " + fromNodeId + " doesnot exists")
        fromNode.nbrNodes.contains(toNodeId)
    }

    def isEmpty(): Boolean = nodeMap.isEmpty()

    /////////////////////////////////////////////////////////////
    def clear(){
        nodeMap.clear()
    }
    def defrag(){}
    def isOk(){}
}

class SyncUnDirectedGraph[Node <: UnDirectedNode](
        initialSize: Option[Int],
        loadFactor: Option[Float]
    ) extends UnDirectedGraph[Node](initialSize, loadFactor){

    override protected val nodeMap: Int2ObjectMap[Node] =
        Int2ObjectMaps.synchronize(
            new Int2ObjectOpenHashMap[Node](
                initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
                loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )
        )
}

object UnDirectedGraphs {
    trait UnDirectedIterable[Node] extends Graphs.Iterable[Node] {
        protected val nodeMap: Int2ObjectMap[Node]
        protected val initialSize: Option[Int]
        protected val loadFactor: Option[Float]

        def getNodeIter(): UnDirectedNodeIter[Node]
        def getNodeIterFromNode(nodeId: Int): UnDirectedNodeIter[Node]
    }

    trait BiDirectionalIterable[Node] extends UnDirectedIterable[Node] {
        override protected val nodeMap: Int2ObjectMap[Node] =
            new Int2ObjectLinkedOpenHashMap(
                initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
                loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )

        override def getNodeIter(): UnDirectedBiDirectionalNodeIter[Node] = {
            new UnDirectedBiDirectionalNodeIter[Node](this.nodeMap)
        }

        override def getNodeIterFromNode(nodeId: Int): UnDirectedBiDirectionalNodeIter[Node] = {
            require(this.nodeMap.size() != 0, "No node present yet")
            new UnDirectedBiDirectionalNodeIter[Node](this.nodeMap, nodeId)
        }

    }

    trait SyncBiDirectionalIterable[Node] extends BiDirectionalIterable[Node] {
        override protected val nodeMap: Int2ObjectMap[Node] =
        Int2ObjectSortedMaps.synchronize(
            new Int2ObjectLinkedOpenHashMap[Node](
                initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
                loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )
        )
    }

    class UnDirectedBiDirectionalGraph[Node <: UnDirectedNode](
        override protected val initialSize: Option[Int],
        override protected val loadFactor: Option[Float]
    ) extends UnDirectedGraph[Node](initialSize, loadFactor) with BiDirectionalIterable[Node]

    class SyncUnDirectedBiDirectionalGraph[Node <: UnDirectedNode](
        override protected val initialSize: Option[Int],
        override protected val loadFactor: Option[Float]
    ) extends SyncUnDirectedGraph[Node](initialSize, loadFactor) with SyncBiDirectionalIterable[Node]
}

object UnDirectedGraph {

    def apply(
        syncAccess: Option[SyncAccess],
        initialSize: Option[Int],
        loadFactor: Option[Float]) = {

            syncAccess.getOrElse(NoSync()) match {
                case NodeAccessSync() =>
                    new UnDirectedGraph[SyncUnDirectedNode](initialSize, loadFactor)
                case GraphAccessSync() =>
                    new UnDirectedGraph[UnDirectedNode](initialSize, loadFactor)
                case GraphNNodeAccessSync() =>
                    new UnDirectedGraph[SyncUnDirectedNode](initialSize, loadFactor)
                case NoSync() =>
                    new UnDirectedGraph[UnDirectedNode](initialSize, loadFactor)
            }
    }

}
