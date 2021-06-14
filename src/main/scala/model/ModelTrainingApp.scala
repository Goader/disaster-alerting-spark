package model

import org.apache.spark.sql.{functions => f}
import model.pipeline.TweetEmbeddingPipeline
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
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
  val pipeline = TweetEmbeddingPipeline("text")
  val model = pipeline.fit(data)

  data = model.transform(data)
  println("Columns after pipeline: " + data.columns.mkString(", "))

  // cross validation using LogisticRegression
  val lr = new LogisticRegression()
    .setMaxIter(1000)
    .setFeaturesCol("embeddings")
    .setLabelCol("target")

  val paramGrid = new ParamGridBuilder()
    .addGrid(lr.regParam, Array(0.3, 0.1, 0.05, 0.01))
    .addGrid(lr.elasticNetParam, Array(0.0, 0.05, 0.2, 0.5, 0.8, 0.95, 1.0))
    .build()

  val cv = new CrossValidator()
    .setEstimator(lr)
    .setEvaluator(new BinaryClassificationEvaluator()
                    .setLabelCol("target"))
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(5)
    .setParallelism(12)

  println("\nTraining model...")
  val cvModel = cv.fit(data)

  // best hyperparameters for the model
  val bestModel = cvModel.bestModel.asInstanceOf[LogisticRegressionModel]
  println("Regularization coef: " + bestModel.getRegParam)
  println("ElasticNet coef: " + bestModel.getElasticNetParam)

  val result = bestModel.transform(data)
  println("Result columns: " + result.columns.mkString(", "))

  // printing results
  cvModel.avgMetrics.zip(paramGrid).foreach(println)

  // saving the model
  // saving model to resources, so that it can be used in the application
  // it is written this way, as this file is treated as a script, not as a program
  bestModel.write.overwrite.save("src/main/resources/model/logistic_model")
}
