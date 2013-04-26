package com.provoz.graph

import com.provoz.graph.node._
import com.provoz.graph.UnDirectedGraphs._

object TestGraphs {
    def main(args: Array[String]) = {
       new UnDirectedGraph[UnDirectedNode](None, None)
       new UnDirectedGraph[SyncUnDirectedNode](None, None)
       new SyncUnDirectedGraph[UnDirectedNode](None, None)
       new SyncUnDirectedGraph[SyncUnDirectedNode](None, None)

       new UnDirectedGraph[UnDirectedNode](None, None) with BiDirectionalIterable[UnDirectedNode]
       new UnDirectedGraph[SyncUnDirectedNode](None, None) with BiDirectionalIterable[SyncUnDirectedNode]
       new SyncUnDirectedGraph[UnDirectedNode](None, None) with BiDirectionalIterable[UnDirectedNode]
       new SyncUnDirectedGraph[SyncUnDirectedNode](None, None) with BiDirectionalIterable[SyncUnDirectedNode]

       new UnDirectedBiDirectionalGraph[UnDirectedNode](None, None)
       new SyncUnDirectedBiDirectionalGraph[UnDirectedNode](None, None)

       new UnDirectedBiDirectionalGraph[SyncUnDirectedNode](None, None)
       new SyncUnDirectedBiDirectionalGraph[SyncUnDirectedNode](None, None)
    }
}