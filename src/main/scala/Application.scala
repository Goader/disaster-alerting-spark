import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.{SQLContext, SparkSession, functions => f}
import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}

object Application extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  val text = "#RockyFire Update => California Hwy. 20 closed in both directions due to Lake County fire - #CAfire #wildfires"


}
