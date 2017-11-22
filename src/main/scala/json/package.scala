import json.models._
import shapeless._

import scala.language.higherKinds

package object json {
  implicit val BooleanToJson: JsonConvertible[Boolean] = b => JsonBoolean(b)

  implicit val IntToJson: JsonConvertible[Int] = i => JsonNumber(i)

  implicit val DoubleToJson: JsonConvertible[Double] = d => JsonNumber(d)

  implicit val StringToJson: JsonConvertible[String] = s => JsonString(s)

  implicit def SeqToJson[A: JsonConvertible, S[B] <: Seq[B]]: JsonConvertible[S[A]] =
    l => JsonArray(l.map(toJson[A]): _*)

  implicit def OptionToJson[A: JsonConvertible]: JsonConvertible[Option[A]] = {
    case Some(a) => toJson(a)
    case None => JsonNull
  }

  implicit val HNilToJson: JsonConvertible[HNil] = _ => JsonObject()

  implicit def HConsToJson[H: JsonConvertible, T <: HList : JsonConvertible]: JsonConvertible[H :: T] = { hlist =>
    val headJson = hlist.head.toJson
    val tailJson = hlist.tail.toJson.asInstanceOf[JsonObject]

    tailJson + ("key" -> headJson)
  }

  implicit class JsonConversion[A: JsonConvertible](a: A) {
    def toJson: Json = implicitly[JsonConvertible[A]].toJson(a)
  }

  def toJson[A: JsonConvertible](a: A): Json = a.toJson
}
