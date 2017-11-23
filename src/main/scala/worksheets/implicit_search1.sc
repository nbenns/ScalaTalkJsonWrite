case class A(text: String)

implicit class AOps(a: A) {
  def display(): Unit = println(s"displaying: ${a.text}")
}

val a = A("hello")
a.display()