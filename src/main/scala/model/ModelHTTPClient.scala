package model

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import play.api.libs.json._


object ModelHTTPClient {
  def isServerRunning(): Boolean = {
    true
  }

  def predict(text: String): String = {
    val request = Map("text" -> text)
    val jsonString = Json.stringify(Json.toJson(request))

    // add name value pairs to a post object
    val post = new HttpPost("http://127.0.0.1:5000/predict")
    post.addHeader("content-type", "application/json")

    val entity = new StringEntity(jsonString)
    post.setEntity(entity)

    // send the post request
    val client = HttpClientBuilder.create().build()
    val response = client.execute(post)

    val code = response.getStatusLine.getStatusCode

    if (code != 200) {
      return "error"
    }

    val contentInputStream = response.getEntity.getContent
    val content = scala.io.Source.fromInputStream(contentInputStream).getLines.mkString

    val responseMap = Json.parse(content).as[Map[String, String]]

    responseMap.getOrElse("class", "error")
  }
}
