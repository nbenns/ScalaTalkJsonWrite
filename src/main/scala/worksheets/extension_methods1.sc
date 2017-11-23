class A(val text: String) {
  def display(): Unit = println(text)
}

val a = new A("hello")
a.display()