package json

package object models {
  sealed abstract class Json {
    def stringify: String
  }

  case object JsonNull extends Json {
    override def stringify = "null"
  }
  type JsonNull = JsonNull.type

  case class JsonBoolean(b: Boolean) extends Json {
    override def stringify = b.toString
  }

  case class JsonNumber(n: Double) extends Json {
    override def stringify =
      if (n % 1 == 0) n.toInt.toString
      else n.toString
  }

  case class JsonString(s: String) extends Json {
    override def stringify = "\"" + s + "\""
  }

  case class JsonArray(elements: Json*) extends Json {
    override def stringify = "[" + elements.map(_.stringify).mkString(", ") + "]"
  }

  case class JsonObject(pairs: (String, Json)*) extends Json {
    private val stringifyPair = ((k: String, v: Json) => {
      val key = JsonString(k).stringify
      val value = v.stringify

      s"$key: $value"
    }).tupled

    override def stringify = "{" + pairs.map(stringifyPair).mkString(", ") + "}"
  }
}
