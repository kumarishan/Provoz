Provoz
======
Provoz is a graph library implemented in scala
Its aim is to provide functionality at par with SNAP in scala  
  
Inspired from SNAP, Cassovary
  
Implementation Notes
--------------------
To begin with following basic types of graphs will be implemented
similar to SNAP  
Underlining implementation will closely follow SNAP  
Types of graphs  
- Undirected graph
- Directed Graph
- BiPartite Graph
- MultiGraph - undirected and directed _later_

Each graph as node id and its list of neighbouring nodes
for directed graph we have two list In and out nodes. There
is an option to store either or both as in Cassovary.  

Graphs nature will be modified by using stackable modification
(ie using traits wherever applicable) inorder for java compatibility
factory methods will be used to initialize (empty or non empty) graphs with distint properties.  

com.provoz.graph.Graph  
com.provoz.graph.UndirectedGraph  
com.provoz.graph.DirectedGraph  
com.provoz.graph.BiPartiteGraph  
com.provoz.grapg.node.Node  

Node can have synchronized access for addition/deletion of edges  
Graph can have synchronized access for addition/deletion of nodes  
Both synchronization is enabled separately using Type-Safe Builder Pattern  

```scala

// Undirected Graph
UnDirectedGraph graph = graphBuilder init(UnDirected) build

// Synchronized Undirected Graph
UnDirectedGraph graph = graphBuilder init(UnDirected) enableSyncAccess(GrapLevelSync) build

// Synchronized Undirected Graph with Synchronized Node
UnDirectedGraph graph = graphBuilder init(UnDirected) enableSyncAccess(GraphNodeLevelSync) build

// Synchronized Directed Graph - store both direction (default)
DirectedGraph graph = graphBuilder init(Directed) enableSyncAccess(GraphLevelSync) onlyStore(BothDir) build
// or
DirectedGraph graph = graphBuilder init(Directed) enableSyncAccess(GraphLevelSync) build

// Directed Graph - store in direction only
DirectedGraph graph = graphBuilder init(Directed) enableSyncAccess(GraphLevelSync) onlyStore(InNodes)

// BiPartite Graph
BiPartiteGraph graph = graphBuilder init(BiPartite) build

### How Graphs are implemented
```scala
abstract class Graph
abstract object GraphFactory

abstract class UnDirectedGraph extends Graph
object UnDirectedGraph extends GraphFactory # Factory object for UnDirectedGraph
