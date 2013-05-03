package com.provoz.graph

import it.unimi.dsi.fastutil.ints.{IntSet, IntIterator}
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.{Int2ObjectSortedMap, Int2ObjectLinkedOpenHashMap}
import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, Int2ObjectOpenHashMap}
import it.unimi.dsi.fastutil.Hash

import scala.util.Random

import com.provoz.graph.node._

abstract class ADirectedGraph[Node <: DirectedNode](
        _initialSize: Option[Int],
        _loadFactor: Option[Float]
    ) extends IGraph[Node]
      with Graphs.ADataStore[Node] {

    override protected[graph] val initialSize = _initialSize
    override protected[graph] val loadFactor = _loadFactor

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

    def addOrReplaceNode(node: Node): Node = {
        nodeMap.put(node.nodeId, node)
        if(node.nodeId > maxNId) maxNId = node.nodeId
        if(!node.inNodes.isEmpty){
            val it = node.inNodes.iterator
            while(it.hasNext){
                nodeMap.get(it.next).outNodes.add(node.nodeId)
            }
        }
        if(!node.outNodes.isEmpty){
            val it = node.outNodes.iterator
            while(it.hasNext){
                nodeMap.get(it.next).inNodes.add(node.nodeId)
            }
        }
        node
    }

    def addOrFailNewNode(node: Node): Node = {
        require(isNode(node.nodeId) == false,
            "Node with node id " + node.nodeId + "already present")
        addOrReplaceNode(node)
    }

    /////////////////////////////////////////////////////////////
    def addEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode: Node = nodeMap.get(fromNodeId)
        val toNode: Node = nodeMap.get(toNodeId)
        require(fromNode != null || toNode != null,
            "Neither of from or to Node can be non existent id")
        fromNode.outNodes.add(toNodeId)
        toNode.inNodes.add(fromNodeId)
        numEdges += 1
    }

    def addEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode: Node = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "From node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map( id => {
            val toNode: Node = nodeMap.get(id)
            if (toNode != null){
                toNode.inNodes.add(fromNodeId)
                fromNode.outNodes.add(id)
                numEdges += 1
            }
        })
    }

    def addEdges(fromNodeId: Int, toNodeIds: IntSet){
        addEdges(fromNodeId, toNodeIds.toIntArray)
    }

    def addEdges(fromNodeIds: Array[Int], toNodeId: Int){
        val toNode: Node = nodeMap.get(toNodeId)
        require(toNode != null,
            "Node with node id " + toNodeId + " doesnot exists")
        fromNodeIds.map( id => {
            val fromNode: Node = nodeMap.get(id)
            if (fromNode != null){
                fromNode.outNodes.add(toNodeId)
                toNode.inNodes.add(id)
                numEdges += 1
            }
        })
    }

    def addEdges(fromNodeIds: IntSet, toNodeId: Int){
        addEdges(fromNodeIds.toIntArray, toNodeId)
    }

    /////////////////////////////////////////////////////////////
    def getNode(nodeId: Int): Node = nodeMap.get(nodeId)
    def getAllNodes(): IntSet = nodeMap.keySet
    def getRandomNode(rand: Random): Node = {
        var randNode: Node = nodeMap.get(rand.nextInt(maxNId))
        while(randNode == null) randNode = nodeMap.get(rand.nextInt(maxNId))
        randNode
    }

    /////////////////////////////////////////////////////////////
    def getNbrsForNode(nodeId: Int): IntSet = {
        val node: Node = nodeMap.get(nodeId)
        require(node != null,
            "Node for node id " + nodeId + " doesnot exists")
        val neighbours = new IntOpenHashSet(node.inNodes)
        neighbours.addAll(node.outNodes)
        neighbours
    }

    def getOutNodesForNode(nodeId: Int): IntSet = {
        val node = nodeMap.get(nodeId)
        require(node != null, "non existent node id " + nodeId)
        node.outNodes
    }

    def getInNodesForNode(nodeId: Int): IntSet = {
        val node = nodeMap.get(nodeId)
        require(node != null, "non existent node id " + nodeId)
        node.inNodes
    }

    /////////////////////////////////////////////////////////////
    def removeEdge(fromNodeId: Int, toNodeId: Int){
        val fromNode: Node = nodeMap.get(fromNodeId)
        val toNode: Node = nodeMap.get(toNodeId)
        fromNode.outNodes.remove(toNodeId)
        toNode.inNodes.remove(fromNodeId)
        numEdges -= 1
    }

    def removeEdges(fromNodeId: Int, toNodeIds: Array[Int]){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "node with node id " + fromNodeId + " doesnot exists")
        toNodeIds.map(id  => {
            val toNode = nodeMap.get(id)
            if(toNode != null){
                fromNode.outNodes.remove(id)
                toNode.inNodes.remove(fromNodeId)
                numEdges -= 1
            }
        })
    }

    def removeEdges(fromNodeId: Int, toNodeIds: IntSet){
        val fromNode = nodeMap.get(fromNodeId)
        require(fromNode != null,
            "node with node id " + fromNodeId + " doesnot exists")
        val it = toNodeIds.iterator
        while(it.hasNext){
            val toNode = nodeMap.get(it.next)
            if(toNode != null){
                fromNode.outNodes.remove(toNode.nodeId)
                toNode.inNodes.remove(fromNodeId)
                numEdges -= 1
            }
        }
    }

    def removeEdges(fromNodeIds: Array[Int], toNodeId: Int){
        val toNode = nodeMap.get(toNodeId)
        require(toNode != null,
            "node with node id " + toNodeId + " doesnot exists")
        fromNodeIds.map(id  => {
            val fromNode = nodeMap.get(id)
            if(fromNode != null){
                fromNode.outNodes.remove(toNodeId)
                toNode.inNodes.remove(id)
                numEdges -= 1
            }
        })
    }

    def removeEdges(fromNodeIds: IntSet, toNodeId: Int){
        val  toNode = nodeMap.get(toNodeId)
        require(toNode != null,
            "node with node id " + toNodeId + " doesnot exists")
        val it = fromNodeIds.iterator
        while(it.hasNext){
            val fromNode = nodeMap.get(it.next)
            if(fromNode != null){
                fromNode.outNodes.remove(toNodeId)
                toNode.inNodes.remove(fromNode.nodeId)
                numEdges -= 1
            }
        }
    }


    /////////////////////////////////////////////////////////////
    def removeNode(nodeId: Int){
        val node = nodeMap.get(nodeId)
        val inIt = node.inNodes.iterator
        while(inIt.hasNext){
            val inNode = nodeMap.get(inIt.next)
            if(inNode != null){
                inNode.outNodes.remove(nodeId)
                numEdges -= 1
            }
        }

        val outIt = node.inNodes.iterator()
        while(outIt.hasNext){
            val outNode = nodeMap.get(outIt.next)
            if(outNode != null){
                outNode.inNodes.remove(nodeId)
                numEdges -= 1
            }
        }
        nodeMap.remove(nodeId)
    }

    def removeNodes(nodeIds: Array[Int]){
        nodeIds.map(id => removeNode(id))
    }

    def removeNodes(nodeIds: IntSet){
        val it = nodeIds.iterator
        while(it.hasNext) removeNode(it.next)
    }

    /////////////////////////////////////////////////////////////
    def isNode(nodeId: Int): Boolean = nodeMap.containsKey(nodeId)

    def isEdge(fromNodeId: Int, toNodeId: Int): Boolean = {
        val fromNode = nodeMap.get(fromNodeId)
        val toNode = nodeMap.get(toNodeId)
        if(fromNode == null || toNode == null) false
        else fromNode.outNodes.contains(toNodeId) && toNode.inNodes.contains(fromNodeId)
    }

    def isEmpty(): Boolean = nodeMap.isEmpty

    /////////////////////////////////////////////////////////////
    def clear(){ nodeMap.clear }
    def defrag(){}
    def isOk(){}
}

object DirectedGraphs {

    trait DirectedIterable[Node <: DirectedNode]
        extends Graphs.Iterable[Node] {

        def getNodeIter(): DirectedNodeIter[Node] =
            new DirectedNodeIter[Node](nodeMap)

    }

    trait BiDirectionalIterable[Node <: DirectedNode]
        extends Graphs.BiDirectionalIterable[Node]{

        override def getNodeIter(): DirectedBiDirectionalNodeIter[Node] = {
            new DirectedBiDirectionalNodeIter[Node](
                this.nodeMap.asInstanceOf[Int2ObjectSortedMap[Node]]
            )
        }

        override def getNodeIterFromNode(nodeId: Int): DirectedBiDirectionalNodeIter[Node] = {
            require(this.nodeMap.size() != 0, "No node present yet")
            new DirectedBiDirectionalNodeIter[Node](
                this.nodeMap.asInstanceOf[Int2ObjectSortedMap[Node]],
                nodeId
            )
        }
    }

}


class DirectedGraph[Node <: DirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends ADirectedGraph[Node](_initialSize, _loadFactor)
  with DirectedGraphs.DirectedIterable[Node]
  with Graphs.DataStore[Node]

class SyncDirectedGraph[Node <: DirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends ADirectedGraph[Node](_initialSize, _loadFactor)
  with DirectedGraphs.DirectedIterable[Node]
  with Graphs.SyncDataStore[Node]

class DirectedBiDirectionalGraph[Node <: DirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends ADirectedGraph[Node](_initialSize, _loadFactor)
  with DirectedGraphs.BiDirectionalIterable[Node]
  with Graphs.BiDirectionalDataStore[Node]

class SyncDirectedBiDirectionalGraph[Node <: DirectedNode](
    _initialSize: Option[Int],
    _loadFactor: Option[Float]
) extends ADirectedGraph[Node](_initialSize, _loadFactor)
  with DirectedGraphs.BiDirectionalIterable[Node]
  with Graphs.SyncBiDirectionalDataStore[Node]