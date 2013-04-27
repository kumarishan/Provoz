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

class DirectedNodeIter[Node <: DirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node] {

    override protected val nodeMap = _nodeMap
    override protected val nodeIter = _nodeMap.keySet().iterator
}

class DirectedBiDirectionalNodeIter[Node <: DirectedNode](
        _nodeMap: Int2ObjectSortedMap[Node],
        _startNodeId: Int
    ) extends BiDirectionalNodeIter[Node]{

    require(_nodeMap.size != 0,
        "If startNodeId provided then nodeMap cannot be empty")
    require(_nodeMap.containsKey(_startNodeId),
        "Node map doesnot contain the node id " + _startNodeId)

    override protected val startNodeId = _startNodeId
    override protected val nodeMap = _nodeMap
    override protected val nodeIter =
        _nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(_startNodeId)

    def this(_nodeMap: Int2ObjectSortedMap[Node]){
        this(_nodeMap, _nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt)
    }
}