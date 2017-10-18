package json

class JsonNumber(n: Double) extends Json {
  override def stringify =
    if (n % 1 == 0) n.toInt.toString
    else n.toString
}
