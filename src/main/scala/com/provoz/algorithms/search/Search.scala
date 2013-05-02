package com.provoz.algorithms.search

import com.provoz.graph._
import com.provoz.graph.node._

sealed trait FollowDir
case object FollowInDeg extends FollowDir
case object FollowOutDeg extends FollowDir
case object FollowBoth extends FollowDir

/*
 * Not yet final
 */
abstract class Search[Node <: INode, Graph <: IGraph[Node]]{
    def search(startNId: Int, targetNodeId: Int, distLimit: Int, followDir: FollowDir): Int
    def search(startNId: Int, nodeVisitor: (Int, Int, Int) => Boolean, followDir: FollowDir): Int
}
