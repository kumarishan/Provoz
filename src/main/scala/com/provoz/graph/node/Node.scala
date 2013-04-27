package com.provoz.graph.node

import it.unimi.dsi.fastutil.ints.{IntIterator, IntBidirectionalIterator}
import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, Int2ObjectSortedMap}
import it.unimi.dsi.fastutil.ints.{IntSortedSet, IntOpenHashSet, IntSet, IntSets}

abstract class INode

trait NodeIter[Node <: INode] extends Iterator[Node]{

    protected val nodeMap: Int2ObjectMap[Node]
    protected val nodeIter: IntIterator

    def hasNext = nodeIter.hasNext
    def next = nodeMap.get(nodeIter.next)
}

trait BiDirectionalNodeIter[Node <: INode] extends NodeIter[Node]{

    protected val nodeIter: IntBidirectionalIterator
    protected val startNodeId: Int

    def hasPrevious = nodeIter.hasPrevious
    def previous = nodeIter.previous
}