package com.provoz.graph

abstract class Graph {
    type Node <: INode
    type NodeHash
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

    def builder = new SimpleGraphBuilder[FALSE](None, None, None)
}