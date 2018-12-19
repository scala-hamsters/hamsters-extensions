name := "hamsters-union-types"

version := "1.0.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.4"
crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.4")

resolvers += Resolver.url("github repo for hamsters", url("http://scala-hamsters.github.io/hamsters/releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalameta" %% "scalameta" % "1.8.0" % Provided,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
)

addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
scalacOptions ++= List("-Xplugin-require:macroparadise", "-language:higherKinds", "-language:implicitConversions", "-feature")
resolvers += Resolver.bintrayIvyRepo("scalameta", "maven")