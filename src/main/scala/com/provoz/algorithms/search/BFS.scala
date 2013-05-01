package com.provoz.algorithms.search

import it.unimi.dsi.fastutil.ints.{IntHeapPriorityQueue, Int2IntOpenHashMap}

import com.provoz.graph._
import com.provoz.graph.node._

class BreadthFirstSearch[Node <: INode, Graph <: IGraph[Node]](
        private val graph: Graph
    ){
    require(graph.numOfNodes > 0, "Empty graph passed")

    private val queue = new IntHeapPriorityQueue(graph.numOfNodes)
    private val nodeToDistMap = new Int2IntOpenHashMap()
    private var startNodeId = 0


    def search(startNId: Int, targetNodeId: Int = -1, distLimit: Int = Int.MaxValue, followDir: FollowDir): Int = {
        def nodeVisitor(parentNId: Int, visitedNId: Int, distFromSrc: Int): Boolean = {
            if(visitedNId == targetNodeId || distFromSrc >= distLimit){
                return true
            }
            return false
        }
        search(startNId, nodeVisitor _, followDir)
    }

    def search(startNId: Int, nodeVisitor: (Int, Int, Int) => Boolean, followDir: FollowDir): Int = {

        startNodeId = startNId
        require(graph.isNode(startNodeId), "start node id is not in graph")

        nodeToDistMap.clear()
        queue.clear()
        nodeToDistMap.add(startNodeId, 0) // mark source
        queue.enqueue(startNodeId) //enque source to Q

        var maxDist = 0

        while(!queue.isEmpty()){ // while Q is not empty
            val nId = queue.dequeueInt() // dequeue an item from the Q
            val dist = nodeToDistMap.get(nId)
            followDir match {
                case FollowInDeg => {
                    val inNodeIter = graph.getInNodesForNode(nId).iterator
                    while(inNodeIter.hasNext){ // for each in-edge e
                        val inNodeId = inNodeIter.next // get the node on other side of e
                        if(!nodeToDistMap.containsKey(inNodeId)){ // if other node is not marked
                            nodeToDistMap.add(inNodeId, dist + 1) // mark the node
                            maxDist = if (maxDist > dist + 1) maxDist
                                      else dist + 1 // update dist from the source
                            if(nodeVisitor(nId, inNodeId, dist + 1)){
                                return maxDist
                            } // check for condition to continue
                            queue.enqueue(inNodeId) // enqueue the node
                        }
                    }
                }
                case FollowOutDeg => {
                    val outNodeIter = graph.getOutNodesForNode(nId).iterator
                    while(outNodeIter.hasNext){
                        val outNodeId = outNodeIter.next
                        if(!nodeToDistMap.containsKey(outNodeId)){
                            nodeToDistMap.add(outNodeId, dist + 1)
                            maxDist = if (maxDist > dist + 1) maxDist
                                      else dist + 1
                            if(nodeVisitor(nId, outNodeId, dist + 1)){
                                return maxDist
                            }
                            queue.enqueue(outNodeId)
                        }
                    }
                }
                case FollowBoth => {
                    val nbrsNodeIter = graph.getNbrsForNode(nId).iterator
                    while(nbrsNodeIter.hasNext){
                        val nbrNodeId = nbrsNodeIter.next
                        if(!nodeToDistMap.containsKey(nbrNodeId)){
                            nodeToDistMap.add(nbrNodeId, dist + 1)
                            maxDist = if (maxDist > dist + 1) maxDist
                                      else dist + 1
                            if(nodeVisitor(nId, nbrNodeId, dist + 1)){
                                return maxDist
                            }
                            queue.enqueue(nbrNodeId)
                        }
                    }
                }
            }

        }
        return maxDist
    }

}