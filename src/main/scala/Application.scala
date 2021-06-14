import org.apache.spark.sql.SparkSession
import model.pipeline.TweetEmbeddingPipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import streaming.{DataHandler, TwitterStream}
import utils.ResourceManager

object Application extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  val dataHandler = DataHandler()

  val twitterStream = TwitterStream(sparkSession.sparkContext, dataHandler)

  // TODO waiting to stop everything (for example waiting for Ctrl+C)
}
