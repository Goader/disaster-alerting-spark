package pipeline

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StopWordsRemover, Tokenizer, Word2Vec}
import org.apache.spark.sql.Dataset
import pipeline.cleaning._

object TweetEmbeddingPipeline {
  def apply(dataset: Dataset[_]): Pipeline = {
    val editingCol = "textEd"

    val copier = new ColumnCopy()
      .setInputCol("text")
      .setOutputCol(editingCol)

    val ascii = new ASCIIFilter()
      .setInputOutputCol(editingCol)

    val html = new HTMLRemover()
      .setInputOutputCol(editingCol)

    val lower = new LowerCaseTransformer()
      .setInputOutputCol(editingCol)

    val abbrev = new AbbreviationSubstitution()
      .setInputOutputCol(editingCol)

    val mention = new MentionSubstitution()
      .setInputOutputCol(editingCol)

    val number = new NumberSubstitution()
      .setInputOutputCol(editingCol)

    val smiley = new SmileySubstitution()
      .setInputOutputCol(editingCol)

    val punct = new PunctuationRemover()
      .setInputOutputCol(editingCol)

    val tokenizer = new Tokenizer()
      .setInputCol(editingCol)
      .setOutputCol("tokenized")

    // TODO maybe add spell checker

    val stopwords = new StopWordsRemover()
      .setInputCol("tokenized")
      .setOutputCol("tokenized")

    // training word2vec
    val word2vec = new Word2Vec()
      .setInputCol("tokenized")
      .setOutputCol("embeddings")
      .setVectorSize(200)

    // TODO maybe add some other hyperparameters

    // TODO maybe we should not train it here, but outside
    val word2vecModel = word2vec.fit(dataset)

    val pipeline = new Pipeline()
      .setStages(Array(
        copier,
        ascii,
        html,
        lower,
        abbrev,
        mention,
        number,
        smiley,
        punct,
        tokenizer,
        stopwords,
        word2vecModel
      ))

    pipeline
  }
}
