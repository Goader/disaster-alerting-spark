package streaming

import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Duration, StreamingContext}
import twitter4j.conf.ConfigurationBuilder
import utils.ResourceManager
import utils.TwitterAuthKeys._

class TwitterStream private (val ssc: StreamingContext) {
  def start() = {
    ssc.start()
    ssc.awaitTermination()
  }

  def stop() = {
    ssc.stop(false)
  }
}

object TwitterStream {
  def apply(sparkContext: SparkContext, handler: DataHandler): TwitterStream = {
    val ssc = new StreamingContext(sparkContext, Duration(5000))  // 5s

    val authMap = ResourceManager.loadTwitterAuth()
    println(authMap.mkString(","))

    val cb = new ConfigurationBuilder


    new TwitterStream(ssc)
  }
}
