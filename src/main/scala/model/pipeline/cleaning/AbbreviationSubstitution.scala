package model.pipeline.cleaning

import org.apache.spark.sql.{functions => f}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.types.StructType
import model.pipeline.traits.StringMappable
import utils.ResourceManager

class AbbreviationSubstitution(override val uid: String) extends Transformer with StringMappable {
  final val inputOutputCol = new Param[String](this, "inputOutputCol", "The input/output column")

  def setInputOutputCol(value: String): this.type = set(inputOutputCol, value)

  def this() = this(Identifiable.randomUID("AbbreviationSubstitution"))

  override def transform(dataset: Dataset[_]): DataFrame = {
    val abbrevOpt = ResourceManager.loadAbbreviations()
    if (abbrevOpt.isEmpty) {
      dataset.toDF()
    } else {
      var removed = f.col($(inputOutputCol))
      for ((k, v) <- abbrevOpt.get) {
        removed = f.udf((s: String) => s.replaceAll(s"\\b$k\\b", v))
          .apply(removed)
      }
      dataset.drop($(inputOutputCol))
      dataset.withColumn($(inputOutputCol), removed)
    }
  }
}
