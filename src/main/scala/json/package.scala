import json.models._

package object json {
  implicit class BooleanToJson(b: Boolean) extends JsonConvertible {
    override def toJson = JsonBoolean(b)
  }

  implicit class IntToJson(i: Int) extends JsonConvertible {
    override def toJson = JsonNumber(i)
  }

  implicit class DoubleToJson(d: Double) extends JsonConvertible {
    override def toJson = JsonNumber(d)
  }

  implicit class StringToJson(s: String) extends JsonConvertible {
    override def toJson = JsonString(s)
  }

  implicit class SeqToJson[A <: Json](l: Seq[A]) extends JsonConvertible {
    override def toJson = JsonArray(l: _*)
  }
}
