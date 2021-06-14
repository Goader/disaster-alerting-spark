package model.pipeline

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.{RegexTokenizer, StopWordsRemover, Word2Vec}
import model.pipeline.cleaning._
import org.apache.spark.sql.SQLContext
import utils.ResourceManager

object TweetEmbeddingPipeline {
  def apply(inputCol: String): Pipeline = {
    val editingCol = "textEd"

    val copier = new ColumnCopy()
      .setInputCol(inputCol)
      .setOutputCol(editingCol)

    val ascii = new ASCIIFilter()
      .setInputOutputCol(editingCol)

    val html = new HTMLRemover()
      .setInputOutputCol(editingCol)

    val url = new URLSubstitution()
      .setInputOutputCol(editingCol)

    val lower = new LowerCaseTransformer()
      .setInputOutputCol(editingCol)

    val abbrev = new AbbreviationSubstitution()
      .setInputOutputCol(editingCol)

    val smiley = new SmileySubstitution()
      .setInputOutputCol(editingCol)

    val mention = new MentionSubstitution()
      .setInputOutputCol(editingCol)

    val number = new NumberSubstitution()
      .setInputOutputCol(editingCol)

    val punct = new PunctuationRemover()
      .setInputOutputCol(editingCol)

    // TODO add trimmer

    val tokenizer = new RegexTokenizer()
      .setInputCol(editingCol)
      .setOutputCol("tokenized")
      .setPattern("\\s+")

    val stopwords = new StopWordsRemover()
      .setInputCol("tokenized")
      .setOutputCol("cleaned")

    val pipeline = new Pipeline()
      .setStages(Array(
        copier,
        ascii,
        html,
        url,
        lower,
        abbrev,
        smiley,
        mention,
        number,
        punct,
        tokenizer,
        stopwords
      ))

    pipeline
  }

//  def loadTrained(sqlContext: SQLContext): PipelineModel = {
//      apply("text").fit(ResourceManager.loadDataset(sqlContext))
//  }
}
