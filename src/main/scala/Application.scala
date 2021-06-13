import org.apache.spark.sql.SparkSession
import model.pipeline.TweetEmbeddingPipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import utils.ResourceManager

object Application extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  // TODO instance of data handler

  // TODO instance of twitter stream

  // TODO waiting to stop everything (for example waiting for Ctrl+C)
}
