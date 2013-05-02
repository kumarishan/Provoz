package com.provoz.algorithms.search

import it.unimi.dsi.fastutil.ints.{IntHeapPriorityQueue, Int2IntOpenHashMap}
import it.unimi.dsi.fastutil.ints.{IntArrayList, IntArrays}

import com.provoz.graph._
import com.provoz.graph.node._

import scala.util.Random

class BreadthFirstSearch[Node <: INode, Graph <: IGraph[Node]](
        protected val graph: Graph ){
    require(graph.numOfNodes > 0, "Empty graph passed")

    protected[algorithms] val queue = new IntHeapPriorityQueue(graph.numOfNodes)
    protected[algorithms] val nodeToDistMap = new Int2IntOpenHashMap()
    protected var startNodeId = 0


    def search(
        startNId: Int,
        targetNodeId: Int = -1,
        distLimit: Int = Int.MaxValue,
        followDir: FollowDir): Int = {

        def nodeVisitor(parentNId: Int, visitedNId: Int, distFromSrc: Int): Boolean = {
            if(visitedNId == targetNodeId || distFromSrc >= distLimit){
                return true
            }
            return false
        }
        search(startNId, nodeVisitor _, followDir)
    }

    def search(
        startNId: Int,
        nodeVisitor: (Int, Int, Int) => Boolean,
        followDir: FollowDir): Int = {

        startNodeId = startNId
        require(graph.isNode(startNodeId), "start node id is not in graph")

        nodeToDistMap.clear()
        queue.clear()
        nodeToDistMap.put(startNodeId, 0) // mark source
        queue.enqueue(startNodeId) //enque source to Q

        var maxDist = 0

        while(!queue.isEmpty()){ // while Q is not empty
            val nId = queue.dequeueInt() // dequeue an item from the Q
            val dist = nodeToDistMap.get(nId)
            val adjNodeIter = followDir match {
                case FollowInDeg => graph.getInNodesForNode(nId).iterator
                case FollowOutDeg => graph.getOutNodesForNode(nId).iterator
                case FollowBoth => graph.getNbrsForNode(nId).iterator
            }
            while(adjNodeIter.hasNext){
                val adjNodeId: Int = adjNodeIter.next
                if(!nodeToDistMap.containsKey(adjNodeId)){
                    nodeToDistMap.put(adjNodeId, dist + 1)
                    maxDist = if (maxDist > dist + 1) maxDist
                              else dist + 1
                    if(nodeVisitor(nId, adjNodeId, dist + 1)){
                        return maxDist
                    }
                    queue.enqueue(adjNodeId)
                }
            }
        }

        return maxDist
    }

    def getHopsFor(destNId: Int): Int = {
        val dist = nodeToDistMap.get(destNId)
        if (dist != 0) return dist
        else return -1
    }
}