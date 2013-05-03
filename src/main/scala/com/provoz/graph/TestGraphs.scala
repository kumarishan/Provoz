package com.provoz.graph

import generator._
import node._
import UnDirectedGraphs._
import DirectedGraphs._

import scala.util.Random

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

      //Graph Generators
      def nodeBuilder(nId: Int): UnDirectedNode = new UnDirectedNode(nId)
      def graphBuilder(initialSize: Option[Int], loadFactor: Option[Float]) =
          new UnDirectedGraph[UnDirectedNode](initialSize, loadFactor)

      val generator = new DetGraphGenerator(nodeBuilder, graphBuilder)
      generator.create_star(100, false)
      generator.create_ring(100, 4, false)
      generator.create_grid(10, 10, false)
      generator.create_full_connected(100, false, false)
      generator.create_tree(3, 10, false, false)

      val randGenerator = new RandomGraphGenerator(nodeBuilder, graphBuilder)
      randGenerator.create_random(100, 45, false, false, new Random)
      randGenerator.create_barabasi_albert(10, 11, new Random)

    }
}