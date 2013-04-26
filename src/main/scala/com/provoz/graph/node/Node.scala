package com.provoz.graph.node

import it.unimi.dsi.fastutil.ints.{Int2ObjectMap, Int2ObjectSortedMap}
import it.unimi.dsi.fastutil.ints.{IntSortedSet, IntOpenHashSet, IntSet, IntSets}

abstract class INode

abstract class NodeIter[Node] extends Iterator[Node]

// class NodeIter[Node](_nodeMap: Int2ObjectMap[Node])
//     extends Iterator[Node]{

//     protected val nodeMap = _nodeMap
//     protected val nodeIter = nodeMap.keySet().iterator()

//     def hasNext = nodeIter.hasNext()
//     def next = nodeMap.get(nodeIter.next())
// }

trait BiDirectionalNodeIter[Node] extends NodeIter[Node]{
    def hasPrevious
    def previous
}

// class BiDirectionalNodeIter[Node](
//         _nodeMap: Int2ObjectSortedMap[Node],
//         _startNodeId: Int
//     ) extends NodeIter[Node](_nodeMap) {

//     private val startNodeId = _startNodeId
//     require(nodeMap.size() != 0,
//         "If startNodeId provided then nodeMap cannot be empty")
//     override protected val nodeIter =
//         nodeMap.keySet().asInstanceOf[IntSortedSet].iterator(startNodeId)

//     def this(nodeMap: Int2ObjectSortedMap[Node]){
//         this(nodeMap, nodeMap.keySet().asInstanceOf[IntSortedSet].firstInt())
//     }

//     def hasPrevious = nodeIter.hasPrevious()
//     def previous = nodeIter.previous()
// }