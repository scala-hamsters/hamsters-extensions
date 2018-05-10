name := "hamsters-hlists-extensions"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.11"

resolvers += Resolver.url("github repo for hamsters", url("http://scala-hamsters.github.io/hamsters/releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "io.github.scala-hamsters" %% "hamsters" % "2.6.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
