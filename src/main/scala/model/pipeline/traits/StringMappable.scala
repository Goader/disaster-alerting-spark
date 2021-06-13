package model.pipeline.traits

import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param.{Param, ParamMap}
import org.apache.spark.sql.types.{StringType, StructType}

trait StringMappable extends Transformer {
  val inputOutputCol: Param[String]

  override def copy(extra: ParamMap): Transformer = {
    defaultCopy(extra)
  }

  override def transformSchema(schema: StructType): StructType = {
    val idx = schema.fieldIndex($(inputOutputCol))
    val field = schema.fields(idx)
    if (field.dataType != StringType) {
      throw new Exception(s"Input type ${field.dataType} did not match input type StringType")
    }

    schema
  }
}
