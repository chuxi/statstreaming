package cn.edu.zju.king.streaming

import _root_.kafka.serializer.StringDecoder
import cn.edu.zju.king.streaming.util.StreamingExamples
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.mllib.linalg.Vector

/**
 * Created by king on 15-7-15.
 * use direct kafka method to fetch data directly from kafka server
 */
object StatisticApp {

  def functionToCreateContext(checkpointDirectory: String, args: Array[String]): StreamingContext = {
    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("Statistic App")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    ssc.checkpoint(checkpointDirectory)

    streamingProcessing(ssc, args)

    ssc
  }

  def streamingProcessing(ssc: StreamingContext, args: Array[String]): Unit = {
    val Array(brokers, topics) = args

    val topicsSet = topics.split(",").toSet

    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)

    val nums = lines.filter(_._1 == "number").map(_._2)

    nums.foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
//        rdd.foreach(println)
        val dvRDD: RDD[Vector] = rdd.map(i => Vectors.dense(i.split(" ").map(_.toDouble)))
        val summary = Statistics.colStats(dvRDD)
        println(summary.mean)
        println(summary.variance)
      }
    }

  }



  def main(args: Array[String]) {
    if (args.length < 2) {
      System.err.println(s"""
                            |Usage: DirectKafkaWordCount <brokers> <topics>
                            |  <brokers> is a list of one or more Kafka brokers
                            |  <topics> is a list of one or more kafka topics to consume from
                            |
        """.stripMargin)
      System.exit(1)
    }

    StreamingExamples.setStreamingLogLevels()

    val checkpointDirectory = "hdfs://node1:9000/spark/streaming"

    val ssc = StreamingContext.getOrCreate(checkpointDirectory, () => {
      functionToCreateContext(checkpointDirectory, args)
    })

//    streamingProcessing(ssc, args)

    ssc.start()
    ssc.awaitTermination()

  }

}
