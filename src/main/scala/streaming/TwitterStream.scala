package streaming

import org.apache.spark.streaming.StreamingContext

class TwitterStream private (val ssc: StreamingContext, val handler: DataHandler) {
  def start() = {
    ssc.start()
    ssc.awaitTermination()
  }

  def stop() = {
    ssc.stop(false)
  }
}

object TwitterStream {
  def apply(handler: DataHandler): TwitterStream = {
    // TODO create streaming context and so on
    null
  }
}
