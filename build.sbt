name := "statstreaming"

version := "1.0"

scalaVersion := "2.10.4"

//updateOptions := updateOptions.value.withCachedResolution(true)



libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.4.0"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.4.0" % "provided"

libraryDependencies += "org.apache.kafka" % "kafka_2.10" % "0.8.2.1" % "provided"