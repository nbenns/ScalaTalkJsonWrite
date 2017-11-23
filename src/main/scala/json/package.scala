import json.models._
import scala.reflect.runtime.universe._

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

  implicit class OptionToJson[A <: Json](opt: Option[A]) extends JsonConvertible {
    override def toJson = opt match {
      case Some(a) => a
      case None => JsonNull
    }
  }

  implicit class ProductToJson(o: Product) extends JsonConvertible {
    private def cast[T](a: Any, tt: TypeTag[T]): T = a.asInstanceOf[T]

    private def convert[T: TypeTag](a: Any): Json = a match {
      case value: Boolean => value.toJson
      case value: Int => value.toJson
      case value: Double => value.toJson
      case value: String => value.toJson
      case value: Seq[Any] => value.map(convert).toJson
      case value: Option[Any] => value.map(convert).toJson
      case value if typeOf[T] <:< typeOf[Product] =>
        val prod: Product = cast(value, implicitly[TypeTag[Product]])
        ProductToJson(prod).toJson
      case _ => throw new Exception("type not supported")
    }

    private def toFields[T: TypeTag](c: Product): Seq[(String, Json)] = {
      val keys = c.getClass.getDeclaredFields.map(_.getName)
      val z = (keys zip c.productIterator.toList) map {
        case (key, value) => (key, convert(value))
      }

      z
    }

    val fields = toFields(o)

    override def toJson = JsonObject(fields: _*)
  }
}
