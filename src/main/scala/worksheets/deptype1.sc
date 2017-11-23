trait Converter[A] {
  type Out
  def convert(a: A): Out
}

implicit val intConverter: Converter[Int] = new Converter[Int] {
  override type Out = String

  override def convert(a: Int) = "String: " + a.toString
}

implicit val strConverter: Converter[String] = new Converter[String] {
  override type Out = Int

  override def convert(a: String) = a.toInt
}

def convert[A](a: A)(implicit converter: Converter[A]): converter.Out = converter.convert(a)


val s = convert(2)

val i = convert("3")
