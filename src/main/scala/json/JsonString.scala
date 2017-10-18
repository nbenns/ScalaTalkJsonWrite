package json

class JsonString(s: String) extends Json {
  override def stringify = "\"" + s + "\""
}

