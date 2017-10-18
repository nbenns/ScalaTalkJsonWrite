package json

class JsonBoolean(b: Boolean) extends Json {
  override def stringify = b.toString
}
