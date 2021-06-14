import org.apache.spark.sql.SparkSession
import streaming.{DataHandler, TwitterStream}

object Application extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  val dataHandler = DataHandler(sparkSession)

  val twitterStream = TwitterStream(sparkSession.sparkContext, dataHandler)

  try {
    twitterStream.start()
  } catch {
    case e: Throwable => {
      println(e.getMessage)
    }
  } finally {
    twitterStream.stop()
    sparkSession.stop()
  }
}
