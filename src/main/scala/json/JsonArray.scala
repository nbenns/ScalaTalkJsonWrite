package json

class JsonArray(elements: Json*) extends Json {
  override def stringify = "[" + elements.map(_.stringify).mkString(", ") + "]"
}
