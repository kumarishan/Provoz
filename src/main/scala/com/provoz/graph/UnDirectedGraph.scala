package com.provoz.graph

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.Hash

abstract class UnDirectedGraph(
        private val initialSize: Option[Int],
        private val loadFactor: Option[Float]
    ) extends Graph {

    type NodeHash =  Int2ObjectOpenHashMap[Node]

    private val NodeMap: NodeHash =
        new NodeHash(
            initialSize.getOrElse(Hash.DEFAULT_INITIAL_SIZE),
            loadFactor.getOrElse(Hash.DEFAULT_LOAD_FACTOR)
            )
    private val maxNodeId: Long = 0
    private val numOfEdges: Long = 0
}

object UnDirectedGraph{

    def apply(
        syncAccess: Option[SyncAccess],
        initialSize: Option[Int],
        loadFactor: Option[Float]) = {

            syncAccess.getOrElse(NoSync()) match {
                case NodeAccessSync() =>
                    new UnDirectedGraph(initialSize, loadFactor){
                        type Node = SyncUnDirectedNode
                    }
                case GraphAccessSync() =>
                    new UnDirectedGraph(initialSize, loadFactor){
                        type Node = UnDirectedNode
                    }
                case GraphNNodeAccessSync() =>
                    new UnDirectedGraph(initialSize, loadFactor){
                        type Node = SyncUnDirectedNode
                    }
                case NoSync() =>
                    new UnDirectedGraph(initialSize, loadFactor){
                        type Node = UnDirectedNode
                    }
            }
    }

}