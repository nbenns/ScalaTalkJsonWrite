import json.models._
import shapeless._
import shapeless.labelled.FieldType

import scala.language.higherKinds

package object json {
  implicit val BooleanToJson = new JsonConvertible[Boolean] {
    override type Enc = JsonBoolean
    override def toJson(a: Boolean) = JsonBoolean(a)
  }

  implicit val IntToJson = new JsonConvertible[Int] {
    override type Enc = JsonNumber
    override def toJson(a: Int) = JsonNumber(a)
  }

  implicit val DoubleToJson = new JsonConvertible[Double] {
    override type Enc = JsonNumber
    override def toJson(a: Double) = JsonNumber(a)
  }

  implicit val StringToJson = new JsonConvertible[String] {
    override type Enc = JsonString
    override def toJson(a: String) = JsonString(a)
  }

  implicit def SeqToJson[A: JsonConvertible, S[B] <: Seq[B]] = new JsonConvertible[S[A]] {
    override type Enc = JsonArray
    override def toJson(a: S[A]) = JsonArray(a.map(e => json.toJson(e)): _*)
  }

  implicit def OptionToJson[A: JsonConvertible, B <: Json] = new JsonConvertible[Option[A]] {
    override type Enc = Json
    override def toJson(a: Option[A]) = a match {
      case Some(b) => json.toJson(b)
      case None => JsonNull
    }
  }

  implicit val HNilToJson = new JsonConvertible[HNil] {
    override type Enc = JsonObject
    override def toJson(a: HNil) = JsonObject()
  }

  implicit def HConsToJson[K <: Symbol, H, T <: HList]
  (implicit
    headConv: JsonConvertible[H],
    tailConv: JsonConvertible.Aux[T, JsonObject],
    key: Witness.Aux[K]
  ) = new JsonConvertible[FieldType[K, H] :: T] {
    override type Enc = JsonObject
    override def toJson(a: FieldType[K, H] :: T) = {
      val headJson = headConv.toJson(a.head)
      val tailJson = tailConv.toJson(a.tail)

      tailJson + (key.value.name -> headJson)
    }
  }

  implicit class JsonConversion[A, B <: Json](a: A)(implicit conv: JsonConvertible.Aux[A, B]) {
    def toJson: B = conv.toJson(a)
  }

  def toJson[A](a: A)(implicit conv: JsonConvertible[A]): conv.Enc = conv.toJson(a)
}
