
class A(val text: String) {
  def display(): Unit = println(text)
}

implicit class AOps(a: A) {
  def display2(): Unit = println(a.text + "2")
}

val a = new A("hello")
a.display()

// Under the hood
// AOps(a).display2()
a.display2()

