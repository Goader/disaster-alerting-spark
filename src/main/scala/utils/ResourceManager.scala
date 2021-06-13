package utils

import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.sql.{DataFrame, SQLContext}
import play.api.libs.json._

import scala.io.Source

object ResourceManager {
  def loadDataset(sqlContext: SQLContext): DataFrame = {
    // we do not use try/catch here, as if this fails, execution of the program has no sense
    val df = sqlContext.read
      .format("csv")
      .option("header", "true")
      .option("multiline", "true")
      .option("quote", "\"")
      .load(getClass.getResource("/dataset/train.csv").toURI.toString)

    df.drop("id")
      .drop("keyword")
      .drop("location")
  }

  def loadModel(): Option[LogisticRegressionModel] = {
    val resource = getClass.getResource("/model/logistic_model")
    if (resource == null) None
    else {println(resource.toURI.toString); Option(LogisticRegressionModel.load(resource.toURI.toString))}
  }

  def loadAbbreviations(): Option[Map[String, String]] = {
    var ok = true
    var source: Source = null
    var json: JsValue = null

    // using try catch, because even if this fails, we can just skip this cleaning stage
    // suffering only minor effects on results
    try {
      source = Source.fromFile(getClass.getResource("/cleaning/abbrev.json").toURI)
    } catch {
      case e: Throwable => {
        println("ERROR: " + e.getMessage)
        return None
      }
    }

    try {
      json = Json.parse(source.getLines.mkString)
    } catch  {
      case e: Throwable => {
        println("ERROR: " + e.getMessage)
        ok = false
      }
    } finally {
      source.close()
    }

    if (ok) {
      json.asOpt[Map[String, String]]
    } else {
      None
    }
  }
}
