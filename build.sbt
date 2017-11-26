name := "TouchOscToMax"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
// libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.0-M6"
// libraryDependencies += "co.fs2" %% "fs2-io" % "0.10.0-M6"
libraryDependencies += "io.circe" %% "circe-core" % "0.8.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.8.0"
libraryDependencies += "io.circe" %% "circe-parser" % "0.8.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
