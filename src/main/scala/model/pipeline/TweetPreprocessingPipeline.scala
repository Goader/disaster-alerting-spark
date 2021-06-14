package model.pipeline

import java.io.IOException

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.SQLContext
import utils.ResourceManager

object TweetPreprocessingPipeline {
  def apply(inputColumn: String): Pipeline = {
    val word2vec = new Word2Vec()
      .setInputCol("cleaned")
      .setOutputCol("embeddings")
      .setVectorSize(100)
      .setMinCount(1)

    // this can be extended with feature engineering pipeline
    // there is a prepared structure for this, including TweetFeaturePipeline, and feature subpackage

    new Pipeline()
      .setStages(Array(
        TweetEmbeddingPipeline(inputColumn),
        word2vec
      ))
  }

  def loadTrained(sqlContext: SQLContext): PipelineModel = {
    val word2VecModelOpt = ResourceManager.loadWord2VecModel()

    if (word2VecModelOpt.isEmpty) {
      throw new IOException("Word2Vec model file has not been found or is corrupted")
    }

    val pipeline = new Pipeline()
      .setStages(Array(
        TweetEmbeddingPipeline("text"),
        word2VecModelOpt.get
      ))

    pipeline.fit(ResourceManager.loadDataset(sqlContext))
  }
}
