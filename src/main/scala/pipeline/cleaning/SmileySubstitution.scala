package pipeline.cleaning

import org.apache.spark.sql.{functions => f}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.Param
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset}
import pipeline.traits.StringMappable

class SmileySubstitution(override val uid: String) extends Transformer with StringMappable {
  final val inputOutputCol = new Param[String](this, "inputOutputCol", "The input/output column")

  def setInputOutputCol(value: String): this.type = set(inputOutputCol, value)

  def this() = this(Identifiable.randomUID("SmileySubstitution"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    val eyes = "[8:=;x]"
    val nose = "['`-]"
    val smileyRegexes = Map(
      s"$eyes$nose?[(\\\\/]" -> "SADFACE",
      s"$eyes$nose?[)dDp]" -> "SMILE",
      "<3" -> "LOVE"
    )

    var substituted = f.col($(inputOutputCol))
    for ((reg, sub) <- smileyRegexes) {
      substituted = f.udf((s: String) => s.replaceAll(reg, sub))
        .apply(substituted)
    }
    dataset.drop($(inputOutputCol))
    dataset.withColumn($(inputOutputCol), substituted)
  }
}
