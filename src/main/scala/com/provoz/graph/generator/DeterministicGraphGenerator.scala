package com.provoz.graph.generator

import com.provoz.graph._
import node._

class DetGraphGenerator[Node <: INode, Graph <: IGraph[Node]] (
        private val nodeBuilder: (Int) => Node,
        private val graphBuilder: (Option[Int], Option[Float]) => Graph
    ){

    def create_star(nNodes: Int, directed: Boolean): Graph = {
        require(nNodes > 0, "Number of nodes must be greated than zero")
        val graph = graphBuilder(Some(nNodes), None)
        graph.addOrReplaceNode(nodeBuilder(0))
        for(i <- 1 until nNodes){
            graph.addOrReplaceNode(nodeBuilder(i))
            graph.addEdge(0, i)
            if(directed) graph.addEdge(i, 0)
        }
        return graph
    }

    def create_ring(nNodes: Int, outDeg: Int, directed: Boolean): Graph = {
        require(nNodes > 0, "Number of nodes must be greated than zero")
        val graph = graphBuilder(Some(nNodes), None)
        for(i <- 0 until nNodes)
            graph.addOrReplaceNode(nodeBuilder(i))
        for(i <- 0 until nNodes){
            var dest = for(j <- 0 until outDeg) yield (i + j + 1) % nNodes
            graph.addEdges(i, dest.toArray)
            if(directed){
                val revDest = for(d <- dest) yield d - 1
                graph.addEdges(revDest.toArray, i)
            }
        }
        return graph
    }

    def create_grid(nRows: Int, nCols: Int, directed: Boolean): Graph = {
        require(nRows > 0 && nCols > 0 , "Either nRows or nCols is non positive")
        val graph = graphBuilder(Some(nRows * nCols), None)
        for(n <- 0 until nRows * nCols) graph.addOrReplaceNode(nodeBuilder(n))
        for(r <- 0 until nRows; c <- 0 until nCols){
            val nId = nCols*r + c
            if(r != nRows - 1){
                graph.addEdge(nId, nId + nCols)
                if(directed) graph.addEdge(nId + nCols - 1, nId)
            }
            if(c != nCols - 1){
                graph.addEdge(nId, nId + 1)
                if(directed) graph.addEdge(nId + 1, nId)
            }
        }
        return graph
    }

    def create_full_connected(nNodes: Int, directed: Boolean, loops: Boolean): Graph = {
        val graph = graphBuilder(Some(nNodes), None)
        for(i <- 0 until nNodes) graph.addOrReplaceNode(nodeBuilder(i))
        for(i <- 0 until nNodes){
            var dest = for(d <- 0 until nNodes;if(d != i || loops)) yield d
            graph.addEdges(i, dest.toArray)
            if(directed) graph.addEdges(dest.toArray, i)
        }
        return graph
    }

    def create_tree(fanOut: Int, levels:Int, directed: Boolean, revDirected: Boolean): Graph = {
        val nNodes: Int = ((Math.pow(fanOut, levels + 1.0D) - 1.0D) / (fanOut - 1.0D)).toInt
        val nEdges: Int = nNodes - 1
        val graph = graphBuilder(Some(nNodes), None)
        for(i <- 0 until nNodes) graph.addOrReplaceNode(nodeBuilder(i))
        val nonLeafNodes: Int = (nNodes.toDouble - Math.pow(fanOut, levels)).toInt
        for( n <- 0 until nonLeafNodes; e <- 1 to fanOut){
            if(directed && revDirected) graph.addEdge(fanOut*n + e, n)
            else graph.addEdge(n, fanOut*n + e)
        }
        return graph
    }

}