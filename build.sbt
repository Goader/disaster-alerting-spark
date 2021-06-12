name := "twitter"

version := "0.1"

scalaVersion := "2.12.10"

val sparkVersion = "3.1.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion
)

val playVersion = "2.9.2"

libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion
