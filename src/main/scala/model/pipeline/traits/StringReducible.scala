package model.pipeline.traits

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

trait StringReducible extends Transformer {
  final val inputCol = new Param[String](this, "inputCol", "The input column")
  final val outputCol = new Param[String](this, "outputCol", "The output column")

  override def copy(extra: ParamMap): Transformer = {
    defaultCopy(extra)
  }

  override def transformSchema(schema: StructType): StructType = {
    val idx = schema.fieldIndex($(inputCol))
    val field = schema.fields(idx)
    if (field.dataType != StringType) {
      throw new Exception(s"Input type ${field.dataType} did not match input type StringType")
    }

    schema.add(StructField($(outputCol), StringType, nullable = false))
  }
}
