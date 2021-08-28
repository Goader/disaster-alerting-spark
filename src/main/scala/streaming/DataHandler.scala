package streaming

import model.ModelHTTPClient
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{Dataset, Row, SparkSession, functions => f}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import twitter4j.Status

class DataHandler private (val sparkSession: SparkSession, output: Dataset[_] => Unit) {
  val textCol = "text"
  val predictionCol = "prediction"


  def handle(rdd: RDD[Status]): Unit = {
    val filteredLang = rdd.filter(status => status.getLang == "en")
    val rowRDD = filteredLang.map((w: Status) => {
      val text = w.getText
      val result = ModelHTTPClient.predict(text)

      Row(text, result)
    })

    val schema = StructType(Seq(
      StructField(textCol, StringType, nullable=false),
      StructField(predictionCol, StringType, nullable=false)
    ))
    val df = sparkSession.createDataFrame(rowRDD, schema)

    output(df)
  }
}

object DataHandler {
  def apply(sparkSession: SparkSession, output: Dataset[_] => Unit): DataHandler = {
    new DataHandler(sparkSession, output)
  }

  def apply(sparkSession: SparkSession): DataHandler = {
    new DataHandler(sparkSession, (dataset: Dataset[_]) => {
      val filtered = dataset.filter(f.col("prediction") === "disaster")
      filtered.select("text").foreach(row => {
        println(row.mkString)
        println("-".repeat(150))
      })
    })
  }
}
