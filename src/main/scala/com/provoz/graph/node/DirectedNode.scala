package com.provoz.graph.node

import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, Int2ObjectSortedMap}
import it.unimi.dsi.fastutil.ints.{IntSortedSet, IntOpenHashSet, IntSet, IntSets}

abstract class IDirectedNode extends INode{
    protected[graph] val inNodes: IntSet
    protected[graph] val outNodes: IntSet
}

class DirectedNode(
        val nodeId: Int
    ) extends IDirectedNode {

    require(nodeId > 0, "Node id cannot be negative")
    protected[graph] val inNodes: IntSet = new IntOpenHashSet()
    protected[graph] val outNodes: IntSet = new IntOpenHashSet()

    def this(node: DirectedNode) = {
        this(node.nodeId)
        this.inNodes.addAll(node.inNodes)
        this.outNodes.addAll(node.outNodes)
    }
}

class SyncDirectedNode(
        nodeId: Int
    ) extends DirectedNode(nodeId) {

    override protected[graph] val inNodes: IntSet =
        IntSets.synchronize(new IntOpenHashSet())

    override protected[graph] val outNodes: IntSet =
        IntSets.synchronize(new IntOpenHashSet())

     def this(node: SyncDirectedNode) = {
        this(node.nodeId)
        this.inNodes.addAll(node.inNodes)
        this.outNodes.addAll(node.outNodes)
    }
}

/*class DirectedNodeIter[Node <: DirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node](_nodeMap) {
    // protected val nodeMap = _nodeMap
    // protected val nodeIter = nodeMap.keySet().iterator()

    // def hasNext = nodeIter.hasNext()
    // def next = nodeMap.get(nodeIter.next())
}

class DirectedBiDirectionalNodeIter[Node <: DirectedNode](
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
}*/

class DirectedNodeIter[Node <: DirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node] {
    protected val nodeMap = _nodeMap
    protected val nodeIter = nodeMap.keySet().iterator()

    def hasNext = nodeIter.hasNext()
    def next = nodeMap.get(nodeIter.next())
}

class DirectedBiDirectionalNodeIter[Node <: DirectedNode](
        _nodeMap: Int2ObjectMap[Node],
        private val startNodeId: Int
    ) extends DirectedNodeIter[Node](_nodeMap){

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