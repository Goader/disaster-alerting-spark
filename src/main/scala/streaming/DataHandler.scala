package streaming

import org.apache.spark.sql.{functions => f}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

// TODO maybe create this as a class, and passing output handle function (then it will be more flexible)
class DataHandler private (val output: Dataset[_] => Unit) {
  def handle(rdd: RDD[_]) = {
    // TODO handle data:
    // predict the values
    // then handle the output: print out specific tweets / send them to gui or smth else
  }
}

object DataHandler {
  def apply(output: Dataset[_] => Unit): DataHandler = {
    new DataHandler(output)
  }

  def apply(): DataHandler = {
    new DataHandler((dataset: Dataset[_]) => {
      val filtered = dataset.filter(f.col("prediction") === 1)
      filtered.select("text").foreach(row => {
        println("Tweet labeled as a disaster information:")
        println(row.mkString)
      })
    })
  }
}
