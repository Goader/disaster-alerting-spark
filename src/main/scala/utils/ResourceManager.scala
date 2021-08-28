package utils

import play.api.libs.json._

import scala.io.Source

object ResourceManager {
  def loadTwitterAuth(): Option[Map[TwitterAuthKeys.TwitterAuthKey, String]] = {
    var ok = true
    var source: Source = null
    var json: JsValue = null

    try {
      source = Source.fromFile(getClass.getResource("/auth/auth.json").toURI)
    } catch {
      case e: Throwable =>
        println("ERROR: " + e.getMessage)
        return None
    }

    try {
      json = Json.parse(source.getLines.mkString)
    } catch  {
      case e: Throwable =>
        println("ERROR: " + e.getMessage)
        ok = false
    } finally {
      source.close()
    }

    if (ok) {
      val keyMap = json.as[Map[String, String]]

      if (!keyMap.contains("ConsumerKey") || !keyMap.contains("ConsumerSecret")
         || !keyMap.contains("AccessKey") || !keyMap.contains("AccessSecret"))
        return None

      Option(Map(
        TwitterAuthKeys.ConsumerKey -> keyMap("ConsumerKey"),
        TwitterAuthKeys.ConsumerSecret -> keyMap("ConsumerSecret"),
        TwitterAuthKeys.AccessKey -> keyMap("AccessKey"),
        TwitterAuthKeys.AccessSecret -> keyMap("AccessSecret")
      ))
    } else {
      None
    }
  }
}
