package pipeline.cleaning

import org.apache.spark.sql.{functions => f}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.Param
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset}
import pipeline.traits.StringMappable

class ColumnCopy(override val uid: String) extends Transformer with StringMappable {
  final val inputCol = new Param[String](this, "inputCol", "The input column")
  final val outputCol = new Param[String](this, "outputCol", "The output column")

  def setInputCol(value: String): this.type = set(inputCol, value)
  def setOutputCol(value: String): this.type = set(outputCol, value)

  def this() = this(Identifiable.randomUID("NumberSubstitution"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    dataset.withColumn($(outputCol), f.col($(inputCol)))
  }
}
