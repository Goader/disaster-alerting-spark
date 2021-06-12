package utils

import play.api.libs.json._

import scala.io.Source

object ResourceManager {
  def readAbbreviations(): Option[Map[String, String]] = {
    var ok = true
    var source: Source = null
    var json: JsValue = null

    try {
      source = Source.fromFile(getClass.getResource("/cleaning/abbrev.json").toURI)
    } catch {
      case e: Throwable => {
        println(e.getMessage)
        return None
      }
    }

    try {
      json = Json.parse(source.getLines.mkString)
    } catch  {
      case e: Throwable => {
        println(e.getMessage)
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
