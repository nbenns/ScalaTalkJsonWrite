
class A(val text: String) {
  def display(): Unit = println(text)
}

implicit class AOps(a: A) {
  def display2(): Unit = println(a.text + "2")
}

val a = new A("hello")
a.display()
a.display2()

