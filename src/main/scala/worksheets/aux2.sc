import scala.language.reflectiveCalls

trait Converter[A] {
  type Out
  def convert(a: A): Out
}

object Converter {
  type Aux[A, B] = Converter[A] { type Out = B }
}

implicit val intConverter = new Converter[Int] {
  override type Out = String

  override def convert(a: Int) = "String: " + a.toString
}

implicit val strConverter = new Converter[String] {
  override type Out = Int

  override def convert(a: String) = a.toInt
}

def convert[A, O](a: A)(implicit c: Converter.Aux[A, O]): c.Out = c.convert(a)


val s = convert(2)

val i = convert("3")
