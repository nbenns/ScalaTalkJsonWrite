package json

class JsonObject(pairs: (String, Json)*) extends Json {
  private val stringifyPair = ((k: String, v: Json) => {
    val key = new JsonString(k).stringify
    val value = v.stringify

    s"$key: $value"
  }).tupled

  override def stringify = "{" + pairs.map(stringifyPair).mkString(", ") + "}"
}
