package com.provoz.graph.node

import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, Int2ObjectSortedMap}
import it.unimi.dsi.fastutil.ints.{IntSortedSet, IntOpenHashSet, IntSet, IntSets}

abstract class IUnDirectedNode extends INode{
    protected[graph] val nbrNodes: IntSet
}

class UnDirectedNode(
        val nodeId: Int
    ) extends IUnDirectedNode {

    require(nodeId > 0, "Node id cannot be negative")
    protected[graph] val nbrNodes: IntSet = new IntOpenHashSet()

    def this(node: UnDirectedNode) = {
        this(node.nodeId)
        this.nbrNodes.addAll(node.nbrNodes)
    }
}

class SyncUnDirectedNode(
        nodeId: Int
    ) extends UnDirectedNode(nodeId) {

    override protected[graph] val nbrNodes: IntSet =
        IntSets.synchronize(new IntOpenHashSet())

    def this(node: SyncUnDirectedNode) = {
        this(node.nodeId)
        this.nbrNodes.addAll(node.nbrNodes)
    }
}
/*
class UnDirectedNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node](_nodeMap) {
    //protected val nodeMap = _nodeMap
    // protected val nodeIter = nodeMap.keySet().iterator()

    // def hasNext = nodeIter.hasNext()
    // def next = nodeMap.get(nodeIter.next())
}

class UnDirectedBiDirectionalNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectSortedMap[Node],
        _startNodeId: Int
    ) extends BiDirectionalNodeIter[Node](_nodeMap, _startNodeId) {

    // require(nodeMap.size() != 0,
    //     "If startNodeId provided then nodeMap cannot be empty")
    // override protected val nodeIter =
    //     nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(startNodeId)

    def this(nodeMap: Int2ObjectSortedMap[Node]){
        this(nodeMap, nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt())
    }

    // def hasPrevious = nodeIter.hasPrevious()
    // def previous = nodeIter.previous()
}
*/
class UnDirectedNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node] {
    protected val nodeMap = _nodeMap
    protected val nodeIter = nodeMap.keySet().iterator()

    def hasNext = nodeIter.hasNext()
    def next = nodeMap.get(nodeIter.next())
}

class UnDirectedBiDirectionalNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectSortedMap[Node],
        private val startNodeId: Int
    ) extends UnDirectedNodeIter[Node](_nodeMap) with BiDirectionalNodeIter[Node] {

    require(nodeMap.size() != 0,
        "If startNodeId provided then nodeMap cannot be empty")
    override protected val nodeIter =
        nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(startNodeId)

    def this(nodeMap: Int2ObjectSortedMap[Node]){
        this(nodeMap, nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt())
    }

    def hasPrevious = nodeIter.hasPrevious()
    def previous = nodeIter.previous()
}