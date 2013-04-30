package com.provoz.graph

import it.unimi.dsi.fastutil.ints.{IntSet, IntIterator}
import it.unimi.dsi.fastutil.ints.{Int2ObjectSortedMaps, Int2ObjectSortedMap, Int2ObjectLinkedOpenHashMap}
import it.unimi.dsi.fastutil.ints.{Int2ObjectMaps, Int2ObjectMap, Int2ObjectOpenHashMap}
import it.unimi.dsi.fastutil.Hash

import scala.util.Random

import com.provoz.graph.node._

abstract class AUnDirectedGraph[Node <: UnDirectedNode](
        _initialSize: Option[Int],
        _loadFactor: Option[Float]
    ) extends Graph[Node]
      with Graphs.ADataStore[Node]{

    protected val initialSize = _initialSize
    protected val loadFactor = _loadFactor

    private var maxNId: Int = 0
    private var numEdges: Long = 0
    private var startNId: Int = 0

    def maxNodeId: Int = {
        if (nodeMap.size == 0) -1
        else {
            while(!isNode(maxNId)) maxNId -= 1
            maxNId
        }
    }

    def startNodeId: Int = {
        if (nodeMap.size == 0) -1
        else {
            while(!isNode(startNId)) startNId += 1
            startNId
        }
    }

    def endNodeId: Int = maxNodeId
    def numOfNodes: Int = nodeMap.size
    def numOfEdges: Long = numEdges
    def suggestNextNodeId: Int = maxNodeId + 1

    /////////////////////////////////////////////////////////////
    def addOrGetNode(node: Node): Node = {
        nodeMap.put(node.nodeId, node)
        if(node.nodeId > maxNId) maxNId = node.nodeId
        if(!node.nbrNodes.isEmpty){
            val it = node.nbrNodes.iterator
            while(it.hasNext){
                nodeMap.get(it.next).nbrNodes.add(node.nodeId)
            }
        }
        node
    }

    def addOrFailNewNode(node: Node): Node = {
        require(isNode(node.nodeId) == false,
            "Node with node id " + node.nodeId + "already present")
        addOrGetNode(node)
    }

    /////////////////////////////////////////////////////////////
    def addEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode = nodeMap.get(fromNodeId)
        val toNode = nodeMap.get(toNodeId)
        require(fromNode != null || toNode != null,
            "Neither of from or to Node can be non existent id")
        fromNode.nbrNodes.add(toNodeId)
        toNode.nbrNodes.add(fromNodeId)
        numEdges += 1
    }

    def addEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "Node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map( id => {
            val toNode = nodeMap.get(id)
            if (toNode != null){
                toNode.nbrNodes.add(fromNodeId)
                fromNode.nbrNodes.add(id)
                numEdges += 1
            }
        })
    }

    def addEdges(fromNodeId: Int, toNodeIds: IntSet){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "Node with node id " + fromNodeId + " doesnot exists")
        val it = toNodeIds.iterator
        while(it.hasNext){
            val toNode = nodeMap.get(it.next)
            if (toNode != null){
                toNode.nbrNodes.add(fromNodeId)
                fromNode.nbrNodes.add(toNode.nodeId)
                numEdges += 1
            }
        }
    }

    def addEdges(fromNodeIds: Array[Int], toNodeId: Int){
        addEdges(toNodeId, fromNodeIds)
    }
    def addEdges(fromNodeIds: IntSet, toNodeId: Int){
        addEdges(toNodeId, fromNodeIds)
    }

    /////////////////////////////////////////////////////////////
    def getNode(nodeId: Int): Node = nodeMap.get(nodeId)
    def getAllNodes(): IntSet = nodeMap.keySet
    def getRandomNode(rand: Random): Node = {
        var randNode = nodeMap.get(rand.nextInt)
        while(randNode == null) randNode = nodeMap.get(rand.nextInt)
        randNode
    }

    /////////////////////////////////////////////////////////////
    def getNbrsForNode(nodeId: Int): IntSet = {
        val node = nodeMap.get(nodeId)
        require(node != null,
            "Node for node id " + nodeId + " doesnot exists")
        node.nbrNodes
    }

    def getOutNodesForNode(nodeId: Int): IntSet = getNbrsForNode(nodeId)
    def getInNodesForNode(nodeId: Int): IntSet = getNbrsForNode(nodeId)

    /////////////////////////////////////////////////////////////
    def removeEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode = nodeMap.get(fromNodeId)
        val toNode = nodeMap.get(toNodeId)
        require(fromNode != null || toNode != null,
            "Neither of from or to Node can be non existent id")
        fromNode.nbrNodes.remove(toNodeId)
        toNode.nbrNodes.remove(fromNodeId)
        numEdges -= 1
    }

    def removeEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map( id => {
            val toNode = nodeMap.get(id)
            if (toNode != null){
                toNode.nbrNodes.remove(fromNodeId)
                numEdges -= 1
            }
            fromNode.nbrNodes.remove(id)
        })
    }

    def removeEdges(fromNodeId: Int, toNodeIds: IntSet){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "node with node id " + fromNodeId + " doesnot exists")
        val it = toNodeIds.iterator
        while(it.hasNext){
            val toNode = nodeMap.get(it.next)
            if (toNode != null){
                toNode.nbrNodes.remove(fromNodeId)
                numEdges -= 1
            }
            fromNode.nbrNodes.remove(toNode.nodeId)
        }
    }

    def removeEdges(fromNodeIds: Array[Int], toNodeId: Int){
        removeEdges(toNodeId, fromNodeIds)
    }
    def removeEdges(fromNodeIds: IntSet, toNodeId: Int){
        removeEdges(toNodeId, fromNodeIds)
    }

    /////////////////////////////////////////////////////////////
    def removeNode(nodeId: Int){
        val node = nodeMap.get(nodeId)
        if (node != null){
            val iterator = node.nbrNodes.iterator
            while(iterator.hasNext) {
                val nbrNode = nodeMap.get(iterator.nextInt)
                if (nbrNode != null){
                    nbrNode.nbrNodes.remove(nodeId)
                    numEdges -= 1
                }
            }
            nodeMap.remove(nodeId)
        }
    }

    def removeNodes(nodeIds: Array[Int]){
        nodeIds.map(id => removeNode(id))
    }

    def removeNodes(nodeIds: IntSet){
        val it = nodeIds.iterator
        while(it.hasNext) removeNode(it.nextInt)
    }

    /////////////////////////////////////////////////////////////
    def isNode(nodeId: Int): Boolean = nodeMap.containsKey(nodeId)

    def isEdge(fromNodeId: Int, toNodeId: Int): Boolean = {
        val fromNode = nodeMap.get(fromNodeId)
        val toNode = nodeMap.get(toNodeId)
        if(fromNode == null || toNode == null) false
        else fromNode.nbrNodes.contains(toNodeId)
    }

    def isEmpty(): Boolean = nodeMap.isEmpty

    /////////////////////////////////////////////////////////////
    def clear(){ nodeMap.clear }
    def defrag(){}
    def isOk(){}
}

object UnDirectedGraphs {

    // Iterable Traits for UnDirectedGraphs

    trait UnDirectedIterable[Node <: UnDirectedNode]
        extends Graphs.Iterable[Node] {

        def getNodeIter(): UnDirectedNodeIter[Node] =
            new UnDirectedNodeIter[Node](nodeMap)

    }

    trait BiDirectionalIterable[Node <: UnDirectedNode]
        extends Graphs.BiDirectionalIterable[Node] {

        override def getNodeIter(): UnDirectedBiDirectionalNodeIter[Node] = {
            new UnDirectedBiDirectionalNodeIter[Node](
                this.nodeMap.asInstanceOf[Int2ObjectSortedMap[Node]]
            )
        }

        override def getNodeIterFromNode(nodeId: Int): UnDirectedBiDirectionalNodeIter[Node] = {
            require(this.nodeMap.size() != 0, "No node present yet")
            new UnDirectedBiDirectionalNodeIter[Node](
                this.nodeMap.asInstanceOf[Int2ObjectSortedMap[Node]],
                nodeId
            )
        }

    }

}

class UnDirectedGraph[Node <: UnDirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends AUnDirectedGraph[Node](_initialSize, _loadFactor)
  with UnDirectedGraphs.UnDirectedIterable[Node]
  with Graphs.DataStore[Node]

class SyncUnDirectedGraph[Node <: UnDirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends AUnDirectedGraph[Node](_initialSize, _loadFactor)
  with UnDirectedGraphs.UnDirectedIterable[Node]
  with Graphs.SyncDataStore[Node]

class UnDirectedBiDirectionalGraph[Node <: UnDirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends AUnDirectedGraph[Node](_initialSize, _loadFactor)
  with UnDirectedGraphs.BiDirectionalIterable[Node]
  with Graphs.BiDirectionalDataStore[Node]

class SyncUnDirectedBiDirectionalGraph[Node <: UnDirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends AUnDirectedGraph[Node](_initialSize, _loadFactor)
  with UnDirectedGraphs.BiDirectionalIterable[Node]
  with Graphs.SyncBiDirectionalDataStore[Node]