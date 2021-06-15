package model

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
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
    .addGrid(pipeline.getStages.last.asInstanceOf[RandomForestClassifier].numTrees,
      Array(50, 100, 200, 400, 800))
    .addGrid(pipeline.getStages.last.asInstanceOf[RandomForestClassifier].maxDepth,
      Array(5, 8, 14, 22, 30))
    .build()

  val cv = new CrossValidator()
    .setEstimator(pipeline)
    .setEvaluator(new MulticlassClassificationEvaluator()
                    .setLabelCol("target"))
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(5)
    .setParallelism(12)

  println("\nTraining model...")
  val cvModel = cv.fit(data)

  // best hyperparameters for the model
  val bestModel = cvModel.bestModel.asInstanceOf[PipelineModel]
  val word2VecModel = bestModel.stages.head.asInstanceOf[PipelineModel].stages.last.asInstanceOf[Word2VecModel]
  val clfModel = bestModel.stages.last.asInstanceOf[RandomForestClassificationModel]
  println("Number of trees: " + clfModel.getNumTrees)
  println("Max depth: " + clfModel.getMaxDepth)

  val result = bestModel.transform(data)
  println("Result columns: " + result.columns.mkString(", "))

  // printing results
  cvModel.avgMetrics.zip(paramGrid).foreach(println)

  // saving the models
  // saving model to resources, so that it can be used in the application
  // it is written this way, as this file is treated as a script, not as a program
  word2VecModel.write.overwrite.save("src/main/resources/model/word2vecrf_model")
  clfModel.write.overwrite.save("src/main/resources/model/randomforest_model")
}
