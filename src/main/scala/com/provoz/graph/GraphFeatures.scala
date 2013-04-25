package com.provoz.graph

sealed abstract class GraphType
case class UnDirected(
    initialSize: Option[Int] = None,
    loadFactor: Option[Float] = None
    ) extends GraphType

case class Directed(
    initialSize: Option[Int] = None,
    loadFactor: Option[Float] = None
    ) extends GraphType

case class BiPartite(
    initialSize: Option[Int] = None,
    loadFactor: Option[Float] = None
    ) extends GraphType

sealed abstract class SyncAccess
case class GraphAccessSync extends SyncAccess
case class GraphNNodeAccessSync extends SyncAccess
case class NodeAccessSync extends SyncAccess
case class NoSync extends SyncAccess


sealed abstract class StoredNodesDir
case class InNodes extends StoredNodesDir
case class OutNodes extends StoredNodesDir
case class BothDir extends StoredNodesDir
