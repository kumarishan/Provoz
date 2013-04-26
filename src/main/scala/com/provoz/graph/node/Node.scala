package com.provoz.graph.node

abstract class INode

abstract class NodeIter[Node] extends Iterator[Node]

trait BiDirectionalNodeIter[Node] extends NodeIter[Node]{
    def hasPrevious
    def previous
}