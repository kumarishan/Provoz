package com.provoz.algorithms.search

import it.unimi.dsi.fastutil.ints.{IntArrayList, Int2IntOpenHashMap}

import com.provoz.graph._
import com.provoz.graph.node._

class DepthFirstSearch[Node <: INode, Graph <: IGraph[Node]](
        protected val graph: Graph
    ) extends Search[Node, Graph] {

    require(graph.numOfNodes > 0, "Empty graph passed")

    protected[algorithms] val stack = new IntArrayList(graph.numOfNodes)
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
        stack.clear()

        nodeToDistMap.put(startNodeId, 0) // mark source
        stack.push(startNodeId) //push source to S
        var maxDist = 0

        while(!stack.isEmpty()){ // while S is not empty
            val nId = stack.popInt() // pop an item from the Q
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
                    stack.push(adjNodeId)
                }
            }
        }
        return maxDist
    }

    def getHopsFor(destNId: Int): Int = {
        val dist = nodeToDistMap.get(destNId)
        if(dist != 0) return dist
        else return -1
    }

}

class DirectedDFS[Node <: DirectedNode, Graph <: ADirectedGraph[Node]](
        _graph: Graph
    ) extends DepthFirstSearch[Node, Graph](_graph){

    import DepthFirstSearch._

    /*
     * <= 0 -> discovered, abs will give its distance
     * >= 0 -> explored
    */
    val nodeStateMap = new Int2IntOpenHashMap()

    def search(
        startNId: Int,
        nodeVisitor: (Int, Int, Int) => Boolean,
        edgeVisitor: (Int, Int, EdgeType) => Unit):Int = {

        startNodeId = startNId
        require(graph.isNode(startNodeId), "start node id is not in graph")

        nodeStateMap.clear()
        stack.clear()

        nodeStateMap.put(startNodeId, 0)
        stack.push(startNodeId)
        var maxDist = 0

        while(!stack.isEmpty){
            val nId = stack.popInt
            val state = nodeStateMap.get(nId)
            val outIter = graph.getOutNodesForNode(nId).iterator
            while(outIter.hasNext){
                val outNId: Int = outIter.next
                if(!nodeStateMap.containsKey(outNId)){
                    edgeVisitor(nId, outNId, TreeEdge)
                    val outNDist = state.abs + 1
                    nodeStateMap.put(outNId, - outNDist)
                    maxDist = if (maxDist > outNDist) maxDist
                              else outNDist + 1
                    if(nodeVisitor(nId, outNId, outNDist)) return maxDist
                    stack.push(outNId)
                } else if(nodeStateMap.get(outNId) <= 0){
                    edgeVisitor(nId, outNId, BackEdge)
                } else {
                    edgeVisitor(nId, outNId, CrossEdge)
                }
            }
            nodeStateMap.put(nId, state.abs)
        }
        return maxDist
    }
}

class UnDirectedDFS[Node <: UnDirectedNode, Graph <: AUnDirectedGraph[Node]](
    _graph: Graph
    ) extends DepthFirstSearch[Node, Graph](_graph){

    import DepthFirstSearch._

    /*
     * <= 0 -> discovered, abs will give its distance
     * >= 0 -> explored
    */
    val nodeStateMap = new Int2IntOpenHashMap()

    def search(
        startNId: Int,
        nodeVisitor: (Int, Int, Int) => Boolean,
        edgeVisitor: (Int, Int, EdgeType) => Unit ): Int = {

        startNodeId = startNId
        require(graph.isNode(startNodeId), "start node id is not in graph")

        nodeStateMap.clear()
        stack.clear()

        nodeStateMap.put(startNodeId, 0)
        stack.push(startNodeId)
        var maxDist = 0

        while(!stack.isEmpty){
            val nId = stack.popInt
            val state = nodeStateMap.get(nId)
            val outIter = graph.getOutNodesForNode(nId).iterator
            while(outIter.hasNext){
                val outNId: Int = outIter.next
                if(!nodeStateMap.containsKey(outNId)){
                    edgeVisitor(nId, outNId, TreeEdge)
                    val outNDist = state.abs + 1
                    nodeStateMap.put(outNId, - outNDist)
                    maxDist = if (maxDist > outNDist) maxDist
                              else outNDist + 1
                    if(nodeVisitor(nId, outNId, outNDist)) return maxDist
                    stack.push(outNId)
                } else if(nodeStateMap.get(outNId) <= 0){
                    edgeVisitor(nId, outNId, BackEdge)
                }
            }
            nodeStateMap.put(nId, state.abs)
        }
        return maxDist
    }
}

object DepthFirstSearch {
    sealed trait EdgeType
    case object TreeEdge extends EdgeType
    case object BackEdge extends EdgeType
    case object CrossEdge extends EdgeType
}