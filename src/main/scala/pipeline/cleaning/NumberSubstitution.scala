package pipeline.cleaning

import org.apache.spark.sql.{functions => f}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.Param
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset}
import pipeline.traits.StringMappable

class NumberSubstitution(override val uid: String) extends Transformer with StringMappable {
  final val inputOutputCol = new Param[String](this, "inputOutputCol", "The input/output column")

  def setInputOutputCol(value: String): this.type = set(inputOutputCol, value)

  def this() = this(Identifiable.randomUID("NumberSubstitution"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    // TODO checkout numbers regex
    val filtered = f.udf((s: String) => s.replaceAll("[-+]?[.\\d]*[\\d]+[:,.\\d]*", "NUMBER"))
      .apply(f.col($(inputOutputCol)))
    dataset.drop($(inputOutputCol))
    dataset.withColumn($(inputOutputCol), filtered)
  }
}
