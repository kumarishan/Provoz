package com.provoz.graph

import it.unimi.dsi.fastutil.ints.{IntSet, IntCollection}
import com.provoz.graph.node._

import scala.util.Random

abstract class Graph[Node] extends Graphs.Iterable[Node] {

    def maxNodeId: Int
    def startNodeId: Int
    def endNodeId: Int
    def numOfNodes: Int
    def numOfEdges: Long
    def suggestNextNodeId: Int

    def addOrGetNode(node: Node): Node
    def addOrFailNewNode(node: Node): Node

    def addEdge(fromNodeId: Int, toNodeId: Int)
    def addEdges(fromNodeId: Int, toNodeIds: Array[Int])
    def addEdges(fromNodeId: Int, toNodeIds: IntCollection)
    def addEdges(fromNodeIds: Array[Int], toNodeId: Int)
    def addEdges(fromNodeIds: IntCollection, toNodeId: Int)

    def getNode(nodeId: Int): Node
    def getAllNodes(): IntSet
    def getRandomNode(rand: Random): Node

    def getNbrsForNode(nodeId: Int): Array[Int]
    def getOutNodesForNode(nodeId: Int): Array[Int]
    def getInNodesForNode(nodeId: Int): Array[Int]

    def removeEdge(fromNodeId: Int, toNodeId: Int)
    def removeEdges(fromNodeId: Int, toNodeIds: Array[Int])
    def removeEdges(fromNodeId: Int, toNodeIds: IntCollection)
    def removeEdges(fromNodeIds: Array[Int], toNodeId: Int)
    def removeEdges(fromNodeIds: IntCollection, toNodeId: Int)

    def removeNode(nodeId: Int)
    def removeNodes(nodeIds: Array[Int])
    def removeNodes(nodeIds: IntCollection)

    def isNode(nodeId: Int): Boolean
    def isEdge(fromNodeId: Int, toNodeId: Int): Boolean
    def isEmpty(): Boolean

    def clear()
    def defrag()
    def isOk()
}

object Graphs {
    trait Iterable[Node] {
        def getNodeIter(): NodeIter[Node]
        def getNodeIterFromNode(nodeId: Int): NodeIter[Node]
    }
}
object GraphBuilder {
    abstract class TRUE
    abstract class FALSE

    class SimpleGraphBuilder[HAS_TYPE](
        val graphType: Option[GraphType],
        val syncAccess: Option[SyncAccess],
        val storedNodesDir: Option[StoredNodesDir]) {

        def init(gTy: GraphType) =
            new SimpleGraphBuilder[TRUE](Some(gTy), syncAccess, storedNodesDir)

        def enableSyncAccess(acc: SyncAccess) =
            new SimpleGraphBuilder[HAS_TYPE](graphType, Some(acc), storedNodesDir)

        def onlyStore(sNdDir: StoredNodesDir) =
            new SimpleGraphBuilder[HAS_TYPE](graphType, syncAccess, Some(sNdDir))
    }

    implicit def enableBuild(builder: SimpleGraphBuilder[TRUE]) = new {
        def build() =
            builder.graphType.get match {
                case UnDirected(initialSize, loadFactor) =>
                    UnDirectedGraph(builder.syncAccess, initialSize, loadFactor)
                case Directed(initialSize, loadFactor) =>
                    UnDirectedGraph(builder.syncAccess, initialSize, loadFactor)
                    // DirectedGraph(builder.storedNodesDir.get, builder.syncAccess.get, initialSize, isStatic)
                case BiPartite(initialSize, loadFactor) =>
                    UnDirectedGraph(builder.syncAccess, initialSize, loadFactor)
                    // BiPartite(builder.storedNodesDir.get, builder.syncAccess.get, initialSize, isStatic)
            }
    }

    def simpleGraphBuilder = new SimpleGraphBuilder[FALSE](None, None, None)
}