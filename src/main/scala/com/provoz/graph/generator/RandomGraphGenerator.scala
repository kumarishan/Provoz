package com.provoz.graph.generator

import it.unimi.dsi.fastutil.ints.IntArrayList

import com.provoz.graph._
import node._

import scala.util.Random

/*
 * All implementation match their counterpart in SNAP
 */
class RandomGraphGenerator[Node <: INode, Graph <: IGraph[Node]](
    private val nodeBuilder: (Int) => Node,
    private val graphBuilder: (Option[Int], Option[Float]) => Graph) {

    def create_random(nNodes: Int, nEdges: Int, directed: Boolean, loop: Boolean, rand: Random): Graph = {
        require(nNodes > 0 && nEdges > 0, "number of nodes and edges cannot be non positive")
        require((nNodes - 1.0D) / 2 * (if(directed) 2 else 1) >= nEdges/nNodes, "Not enough nodes for edges")

        val graph = graphBuilder(Some(nNodes), None)
        for(i <- 0 until nNodes) graph.addOrReplaceNode(nodeBuilder(i))
        var edgesLeft = nEdges
        while(edgesLeft > 0){
            val srcNId = rand.nextInt(nNodes)
            val destNId = rand.nextInt(nNodes)
            if((srcNId != destNId || loop) && !graph.isEdge(srcNId, destNId)){
                graph.addEdge(srcNId, destNId)
                if(directed) graph.addEdge(destNId, srcNId)
                edgesLeft -= 1
            }
        }
        return graph
    }

    def create_barabasi_albert(nNodes: Int, nodeOutDeg: Int, rand: Random): Graph = {
        require(nNodes >= 2, "number of nodes cannot be less than 2")
        val graph = graphBuilder(Some(nNodes), None)
        val nodeIdList = new IntArrayList(nNodes*nodeOutDeg)

        graph.addOrReplaceNode(nodeBuilder(0))
        graph.addOrReplaceNode(nodeBuilder(1))
        nodeIdList.add(0)
        nodeIdList.add(1)

        for(n <- 2 until nNodes){
            val min = Math.min(n, nodeOutDeg)
            val destNodes = for(i <- 0 until min) yield nodeIdList.get(rand.nextInt(nodeIdList.size)).intValue
            println(destNodes)
            val newNId = graph.suggestNextNodeId
            graph.addOrReplaceNode(nodeBuilder(newNId))
            graph.addEdges(newNId, destNodes.toArray)
            nodeIdList.add(newNId)
            for(d <- destNodes) nodeIdList.add(d)
        }
        return graph
    }
}