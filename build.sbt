name := "Provoz - graph library in scala"

version := "0.0.1"

organization := "com.xyris"

scalaVersion := "2.9.2"

retrieveManaged := true

libraryDependencies += "it.unimi.dsi" % "fastutil" % "6.5.4"

mainClass := Some("com.provoz.graph.TestGraphs")