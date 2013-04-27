package com.provoz.graph

import com.provoz.graph.node._
import com.provoz.graph.UnDirectedGraphs._
import com.provoz.graph.DirectedGraphs._

object TestGraphs {
    def main(args: Array[String]) = {
      // UnDirected Graphs
      new UnDirectedGraph[UnDirectedNode](None, None)
      new UnDirectedGraph[SyncUnDirectedNode](None, None)
      new SyncUnDirectedGraph[UnDirectedNode](None, None)
      new SyncUnDirectedGraph[SyncUnDirectedNode](None, None)

      new UnDirectedBiDirectionalGraph[UnDirectedNode](None, None)
      new SyncUnDirectedBiDirectionalGraph[UnDirectedNode](None, None)

      new UnDirectedBiDirectionalGraph[SyncUnDirectedNode](None, None)
      new SyncUnDirectedBiDirectionalGraph[SyncUnDirectedNode](None, None)


      // Directed Graphs
      new DirectedGraph[DirectedNode](None, None)
      new DirectedGraph[SyncDirectedNode](None, None)
      new SyncDirectedGraph[DirectedNode](None, None)
      new SyncDirectedGraph[SyncDirectedNode](None, None)

      new DirectedBiDirectionalGraph[DirectedNode](None, None)
      new SyncDirectedBiDirectionalGraph[DirectedNode](None, None)

      new DirectedBiDirectionalGraph[SyncDirectedNode](None, None)
      new SyncDirectedBiDirectionalGraph[SyncDirectedNode](None, None)
    }
}