import org.apache.spark.sql.SparkSession
import model.pipeline.TweetEmbeddingPipeline
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import utils.ResourceManager

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

  val pipeline = TweetEmbeddingPipeline("text")
  val model = pipeline.fit(documentDF)
  val res2 = model.transform(documentDF)
  res2.select("*").take(3).foreach(println)

  val lr2 = LogisticRegressionModel.read.load("file:/C:/FILES_IN_USE/prj/twitter/target/scala-2.12/classes/model/logistic_model")

  // just debugging, make sure it won't get into final code
  val lrOpt = ResourceManager.loadModel()
  if (lrOpt.isEmpty) {
    println("ERROR: train a model first")
    System.exit(-1);
  }

  val lr = lrOpt.get
  println(lr.getRegParam)
  println(lr.getElasticNetParam)
}
