import scala.language.reflectiveCalls

trait Converter[A] {
  type Out
  def convert(a: A): Out
}

implicit val intConverter = new Converter[Int] {
  override type Out = String

  override def convert(a: Int) = "String: " + a.toString
}

implicit val strConverter = new Converter[String] {
  override type Out = Int

  override def convert(a: String) = a.toInt
}

def convert[A, O](a: A)(implicit c: Converter[A] { type Out = O }): c.Out = c.convert(a)


val s: String = convert(2)

val i: Int = convert("3")
