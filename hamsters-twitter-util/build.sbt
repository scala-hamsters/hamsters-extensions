name := "hamsters-twitter-util"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "2.2.6",
  "com.twitter" %% "util-core" % "6.34.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
