def ListRecursion = {
  def join[A](lst: List[A]): String = lst match {
    // Base case
    case Nil => "Nil"

    // Induction Case
    case (h :: t) => h.toString + ", " + join(t)
  }

  val numList = List(1, 2)
  join(numList)
}


def HListRecursion = {
  import shapeless._

  trait Stringifier[A] {
    def stringify(a: A): String
  }

  def stringify[A](a: A)(implicit stringifier: Stringifier[A]) = stringifier.stringify(a)

  // How to handle each element
  implicit val intStringifier: Stringifier[Int] = i => i.toString
  implicit val strStringifier: Stringifier[String] = identity[String]


  // Base case
  implicit val HNilStringifier: Stringifier[HNil] = _ => "HNil"

  // Induction case
  implicit def HConsStringifier[H, T <: HList]
  (implicit
   headStringifier: Stringifier[H],
   tailStringifier: Stringifier[T]
  ): Stringifier[H :: T] = { hlist =>
    val headStr = headStringifier.stringify(hlist.head)
    val tailStr = tailStringifier.stringify(hlist.tail)

    headStr + " " + tailStr
  }

  stringify(1 :: "abc" :: HNil)
}


ListRecursion

HListRecursion