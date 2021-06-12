package pipeline.cleaning

import org.apache.spark.sql.{functions => f}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.types.StructType
import pipeline.traits.StringMappable

class ASCIIFilter(override val uid: String) extends Transformer with StringMappable {
  final val inputOutputCol = new Param[String](this, "inputOutputCol", "The input/output column")

  def setInputOutputCol(value: String): this.type = set(inputOutputCol, value)

  def this() = this(Identifiable.randomUID("ASCIIFilter"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    var filtered = f.udf((s: String) => s.replaceAll("[^\\u0000-\\u007F]+", ""))
      .apply(f.col($(inputOutputCol)))
    filtered = f.udf((s: String) => s.replaceAll("[^\\x00-\\x7F]+", ""))
      .apply(filtered)
    dataset.drop($(inputOutputCol))
    dataset.withColumn($(inputOutputCol), filtered)
  }
}
