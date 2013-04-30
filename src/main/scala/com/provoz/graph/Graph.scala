package com.provoz.graph

import it.unimi.dsi.fastutil.ints.{IntSet, Int2ObjectMap, Int2ObjectSortedMap}
import it.unimi.dsi.fastutil.ints.{Int2ObjectMaps, Int2ObjectSortedMaps}
import it.unimi.dsi.fastutil.ints.{Int2ObjectOpenHashMap, Int2ObjectLinkedOpenHashMap}
import it.unimi.dsi.fastutil.Hash

import com.provoz.graph.node._

import scala.util.Random

abstract class Graph[Node <: INode] {

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
    def addEdges(fromNodeId: Int, toNodeIds: IntSet)
    def addEdges(fromNodeIds: Array[Int], toNodeId: Int)
    def addEdges(fromNodeIds: IntSet, toNodeId: Int)

    def getNode(nodeId: Int): Node
    def getAllNodes(): IntSet
    def getRandomNode(rand: Random): Node

    def getNbrsForNode(nodeId: Int): IntSet
    def getOutNodesForNode(nodeId: Int): IntSet
    def getInNodesForNode(nodeId: Int): IntSet

    def removeEdge(fromNodeId: Int, toNodeId: Int)
    def removeEdges(fromNodeId: Int, toNodeIds: Array[Int])
    def removeEdges(fromNodeId: Int, toNodeIds: IntSet)
    def removeEdges(fromNodeIds: Array[Int], toNodeId: Int)
    def removeEdges(fromNodeIds: IntSet, toNodeId: Int)

    def removeNode(nodeId: Int)
    def removeNodes(nodeIds: Array[Int])
    def removeNodes(nodeIds: IntSet)

    def isNode(nodeId: Int): Boolean
    def isEdge(fromNodeId: Int, toNodeId: Int): Boolean
    def isEmpty(): Boolean

    def clear()
    def defrag()
    def isOk()
}

object Graphs {
    trait Iterable[Node <: INode]
        extends Graphs.DataStore[Node] {

        def getNodeIter(): NodeIter[Node]
    }

    trait BiDirectionalIterable[Node <: INode]
        extends Graphs.BiDirectionalDataStore[Node] {

        def getNodeIter(): NodeIter[Node]
        def getNodeIterFromNode(nodeId: Int): NodeIter[Node]
    }

    // DataStore Traits for Graphs

    trait ADataStore[Node <: INode] {
        protected val initialSize: Option[Int]
        protected val loadFactor: Option[Float]

        protected val nodeMap: Int2ObjectMap[Node]
    }

    trait DataStore[Node <: INode]
        extends ADataStore[Node] {

        protected val initialSize: Option[Int]
        protected val loadFactor: Option[Float]

        protected val _nodeMap: Int2ObjectMap[Node] =
            new Int2ObjectOpenHashMap[Node](
                initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
                loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )
        protected val nodeMap: Int2ObjectMap[Node] = _nodeMap
    }

    trait SyncDataStore[Node <: INode]
        extends Graphs.DataStore[Node] {

        override protected val nodeMap: Int2ObjectMap[Node] =
            Int2ObjectMaps.synchronize(_nodeMap)
    }

    trait BiDirectionalDataStore[Node <: INode]
        extends ADataStore[Node] {

        protected val initialSize: Option[Int]
        protected val loadFactor: Option[Float]

        protected val _nodeMap: Int2ObjectSortedMap[Node] =
            new Int2ObjectLinkedOpenHashMap(
                initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
                loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )

        protected val nodeMap: Int2ObjectSortedMap[Node] = _nodeMap
    }

    trait SyncBiDirectionalDataStore[Node <: INode]
        extends Graphs.BiDirectionalDataStore[Node] {

        override protected val nodeMap: Int2ObjectSortedMap[Node] =
            Int2ObjectSortedMaps.synchronize(_nodeMap)
    }

}