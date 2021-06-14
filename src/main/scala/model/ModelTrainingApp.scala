package model

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.Word2VecModel
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.{SparkSession, functions => f}
import utils.ResourceManager

object ModelTrainingApp extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter_model")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  // loading data
  var data = ResourceManager.loadDataset(sparkSession.sqlContext)
  data = data.select(data.columns.map {
    case column@"target" =>
      f.col(column).cast("Integer").as(column)
    case column =>
      f.col(column)
  }: _*)

  println("Columns: " + data.columns.mkString(", "))
  data.take(50).foreach(println)
  println("NaN values: " + data.filter(f.isnan(f.col("text"))).count().toString)

  // data cleaning
  val pipeline = ModelPipeline("text")

  val paramGrid = new ParamGridBuilder()
    .addGrid(pipeline.getStages.last.asInstanceOf[LogisticRegression].regParam,
      Array(0.3, 0.1, 0.05, 0.01))
    .addGrid(pipeline.getStages.last.asInstanceOf[LogisticRegression].elasticNetParam,
      Array(0.0, 0.05, 0.2, 0.5, 0.8, 0.95, 1.0))
    .build()

  val cv = new CrossValidator()
    .setEstimator(pipeline)
    .setEvaluator(new BinaryClassificationEvaluator()
                    .setLabelCol("target"))
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(5)
    .setParallelism(12)

  println("\nTraining model...")
  val cvModel = cv.fit(data)

  // best hyperparameters for the model
  val bestModel = cvModel.bestModel.asInstanceOf[PipelineModel]
  val word2VecModel = bestModel.stages.head.asInstanceOf[PipelineModel].stages.last.asInstanceOf[Word2VecModel]
  val logRegModel = bestModel.stages.last.asInstanceOf[LogisticRegressionModel]
  println("Regularization coef: " + logRegModel.getRegParam)
  println("ElasticNet coef: " + logRegModel.getElasticNetParam)

  val result = bestModel.transform(data)
  println("Result columns: " + result.columns.mkString(", "))

  // printing results
  cvModel.avgMetrics.zip(paramGrid).foreach(println)

  // saving the models
  // saving model to resources, so that it can be used in the application
  // it is written this way, as this file is treated as a script, not as a program
  word2VecModel.write.overwrite.save("src/main/resources/model/word2vec_model")
  logRegModel.write.overwrite.save("src/main/resources/model/logistic_model")
}
