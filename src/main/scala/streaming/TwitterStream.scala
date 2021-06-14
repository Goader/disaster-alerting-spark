package streaming

import java.io.IOException

import org.apache.spark.SparkContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Duration, StreamingContext}
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import utils.ResourceManager
import utils.TwitterAuthKeys._

class TwitterStream private (val ssc: StreamingContext) {
  def start(): Unit = {
    ssc.start()
    ssc.awaitTermination()
  }

  def stop(): Unit = {
    ssc.stop(false)
  }
}

object TwitterStream {
  def apply(sparkContext: SparkContext, handler: DataHandler): TwitterStream = {
    val ssc = new StreamingContext(sparkContext, Duration(5000))  // 5s

    val authMapOpt = ResourceManager.loadTwitterAuth()

    if (authMapOpt.isEmpty) {
      throw new IOException("File with authentication keys has not been found or is corrupted")
    }

    val authMap = authMapOpt.get

    val cb = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(authMap(ConsumerKey))
      .setOAuthConsumerSecret(authMap(ConsumerSecret))
      .setOAuthAccessToken(authMap(AccessKey))
      .setOAuthAccessTokenSecret(authMap(AccessSecret))

    val auth = new OAuthAuthorization(cb.build())

    val tweets = TwitterUtils.createStream(ssc, Some(auth))

    // multiple filters can be added like boundingBox (ranges of longitude and latitude)
    // or point_radius (coordinates and a radius)
    // or place_country (country of the tweet)
    // and others
    tweets.foreachRDD(rdd => handler.handle(rdd))

    new TwitterStream(ssc)
  }
}
