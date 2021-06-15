package model

import java.io.IOException

import model.pipeline.TweetPreprocessingPipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.SQLContext
import utils.ResourceManager

object ModelPipeline {
  // using RandomForestClassifier for current model, may be changed later for performance boosting
  def apply(inputCol: String): Pipeline = {
    // TODO load a model, join with cleaning pipelines, create a full pipeline (raw data -> prediction)
    val preprocessing = TweetPreprocessingPipeline(inputCol)

    // Random Forest with some default parameters that have been manually tested to be good enough
    // set hyperparameters can be changed after, for example in a grid search
    val clf = new RandomForestClassifier()
      .setFeaturesCol("embeddings")
      .setLabelCol("target")
      .setNumTrees(800)
      .setMaxDepth(14)

    new Pipeline()
      .setStages(Array(
        preprocessing,
        clf
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
