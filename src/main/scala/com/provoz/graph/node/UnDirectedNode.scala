package com.provoz.graph.node

import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, IntSortedSet, IntOpenHashSet, IntSet, IntSets}

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

    def this() = {
        this(-1)
    }
}

class SyncUnDirectedNode(
        nodeId: Int
    ) extends UnDirectedNode(nodeId) {

    def this(node: SyncUnDirectedNode) = {
        this(node.nodeId)
        this.nbrNodes.addAll(node.nbrNodes)
    }

    def this() = {
        this(-1)
    }

    override protected[graph] val nbrNodes: IntSet = IntSets.synchronize(new IntOpenHashSet())
}

class UnDirectedNodeIter[Node](
        private val nodeMap: Int2ObjectMap[Node]
    ) extends NodeIter[Node] {
    private val nodeIter = nodeMap.keySet().iterator()

    def hasNext = nodeIter.hasNext()
    def next = nodeMap.get(nodeIter.next())
}

class UnDirectedBiDirectionalNodeIter[Node](
        private val nodeMap: Int2ObjectMap[Node],
        private val startNodeId: Int
    ) extends UnDirectedNodeIter[Node](nodeMap) with BiDirectionalNodeIter[Node] {

    require(nodeMap.size() != 0, "If startNodeId provided then nodeMap cannot be empty")
    private val nodeIter = nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(startNodeId)

    def this(nodeMap: Int2ObjectMap[Node]){
        this(nodeMap, nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt())
    }

    def hasPrevious = nodeIter.hasPrevious()

    def previous = nodeIter.previous()
}