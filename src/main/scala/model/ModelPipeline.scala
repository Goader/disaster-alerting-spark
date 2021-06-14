package model

import java.io.IOException

import model.pipeline.TweetPreprocessingPipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SQLContext
import utils.ResourceManager

object ModelPipeline {
  // using LogisticRegression for current model, may be changed later for performance boosting
  def apply(inputCol: String): Pipeline = {
    // TODO load a model, join with cleaning pipelines, create a full pipeline (raw data -> prediction)
    val preprocessing = TweetPreprocessingPipeline(inputCol)

    // Logistic Regression with some default parameters that have been manually tested to be good enough
    // set hyperparameters can be changed after, for example in a grid search
    val lr = new LogisticRegression()
      .setMaxIter(1000)
      .setFeaturesCol("embeddings")
      .setLabelCol("target")
      .setRegParam(0.01)
      .setElasticNetParam(0)

    new Pipeline()
      .setStages(Array(
        preprocessing,
        lr
      ))
  }

  def loadTrained(sqlContext: SQLContext): PipelineModel = {
    val preprocessing = TweetPreprocessingPipeline.loadTrained(sqlContext)
    val modelOpt = ResourceManager.loadModel()

    if (modelOpt.isEmpty) {
      throw new IOException("The model pipeline file has not been found or is corrupted")
    }

    val pipeline = new Pipeline()
      .setStages(Array(
        preprocessing,
        modelOpt.get
      ))

    pipeline.fit(ResourceManager.loadDataset(sqlContext))
  }
}
