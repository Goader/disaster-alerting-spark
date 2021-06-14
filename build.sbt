name := "twitter"

version := "0.1"

scalaVersion := "2.12.10"

// Apache Spark
val sparkVersion = "3.1.1"
val bahirVersion = "2.4.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.bahir" %% "spark-streaming-twitter" % bahirVersion
)

// Play JSON Parser
val playVersion = "2.9.2"

libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion

// Twitter4j - twitter streaming
val twitter4jVersion = "4.0.6"

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % twitter4jVersion,
  "org.twitter4j" % "twitter4j-stream" % twitter4jVersion,
  "org.twitter4j" % "twitter4j-async" % twitter4jVersion
)
