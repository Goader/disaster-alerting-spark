package streaming

import model.ModelPipeline
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{Dataset, Row, SparkSession, functions => f}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import twitter4j.Status

class DataHandler private (val sparkSession: SparkSession, output: Dataset[_] => Unit) {
  val inputCol = "text"
  val model: PipelineModel = ModelPipeline.loadTrained(sparkSession.sqlContext)

  def handle(rdd: RDD[Status]): Unit = {
    val filteredLang = rdd.filter(status => status.getLang == "en")
    val rowRDD = filteredLang.map((w: Status) => Row(w.getText))

    val schema = StructType(Seq(
      StructField(inputCol, StringType, nullable=false)
    ))
    val df = sparkSession.createDataFrame(rowRDD, schema)

    val transformed = model.transform(df)

    output(transformed)
  }
}

object DataHandler {
  def apply(sparkSession: SparkSession, output: Dataset[_] => Unit): DataHandler = {
    new DataHandler(sparkSession, output)
  }

  def apply(sparkSession: SparkSession): DataHandler = {
    new DataHandler(sparkSession, (dataset: Dataset[_]) => {
      val filtered = dataset.filter(f.col("prediction") === 1)
      filtered.select("text").foreach(row => {
        println(row.mkString)
        println("-".repeat(150))
      })
    })
  }
}
