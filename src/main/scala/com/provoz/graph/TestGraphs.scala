package com.provoz.graph

object TestGraphs {
    def main(args: Array[String]) = {
        val graph: UnDirectedGraph = GraphBuilder.builder init(UnDirected()) build()
    }
}