package utils

object TwitterAuthKeys extends Enumeration {
  type TwitterAuthKey = Value

  val ConsumerKey, ConsumerSecret, AccessKey, AccessSecret = Value
}
