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

class UnDirectedNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node]{

    override protected val nodeMap = _nodeMap
    override protected val nodeIter = _nodeMap.keySet().iterator
}

class UnDirectedBiDirectionalNodeIter[Node <: UnDirectedNode](
        _nodeMap: Int2ObjectSortedMap[Node],
        _startNodeId: Int
    ) extends BiDirectionalNodeIter[Node]{

    require(_nodeMap.size() != 0,
        "If startNodeId provided then nodeMap cannot be empty")
    require(_nodeMap.containsKey(_startNodeId),
        "Node map doesnot contain the node id " + _startNodeId)

    override protected val startNodeId = _startNodeId
    override protected val nodeMap = _nodeMap
    override protected val nodeIter =
        _nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(_startNodeId)

    def this(nodeMap: Int2ObjectSortedMap[Node]){
        this(nodeMap, nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt)
    }

}