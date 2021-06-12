import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.{SQLContext, SparkSession, functions => f}
import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import pipeline.TweetEmbeddingPipeline
import pipeline.cleaning.{ASCIIFilter, AbbreviationSubstitution, ColumnCopy, HTMLRemover, LowerCaseTransformer, MentionSubstitution, NumberSubstitution, PunctuationRemover, SmileySubstitution, URLSubstitution}

object Application extends App {
  val sparkSession = SparkSession.builder()
    .appName("twitter")
    .master("local[*]")
    .getOrCreate()

  sparkSession.sparkContext.setLogLevel("ERROR")

  val documentDF = sparkSession.sqlContext.createDataFrame(Seq(
    "#RockyFire Update => California Hwy. 20 closed in both directions due to Lake County fire - #CAfire #wildfires",
    "@bbcmtd Wholesale Marыыпыаввфівфівфїїїапk£ets© a\uD83D\uDE05blaze http://t.co/lHYXEOHY6C",
    "8-\\ <3 x'D <meta class=\"sss\">text</meta> @mention approx asap fb j4f l8r"
  ).map(Tuple1.apply)).toDF("text")

  val pipeline = TweetEmbeddingPipeline()
  val model = pipeline.fit(documentDF)
  val res2 = model.transform(documentDF)
  res2.select("*").take(3).foreach(println)

}
