package pipeline

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{RegexTokenizer, StopWordsRemover, Tokenizer, Word2Vec}
import org.apache.spark.sql.Dataset
import pipeline.cleaning._

object TweetEmbeddingPipeline {
  def apply(): Pipeline = {
    val editingCol = "textEd"

    val copier = new ColumnCopy()
      .setInputCol("text")
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

    val tokenizer = new RegexTokenizer()
      .setInputCol(editingCol)
      .setOutputCol("tokenized")
      .setPattern("\\s+")

    // TODO maybe add spell checker

    val stopwords = new StopWordsRemover()
      .setInputCol("tokenized")
      .setOutputCol("cleaned")

    // training word2vec
    val word2vec = new Word2Vec()
      .setInputCol("cleaned")
      .setOutputCol("embeddings")
      .setVectorSize(100)
      .setMinCount(1)

    // TODO maybe add some other hyperparameters

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
        stopwords,
        word2vec
      ))

    pipeline
  }
}
