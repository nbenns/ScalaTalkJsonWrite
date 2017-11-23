trait MyTypeclass[A] {
  def display(a: A): Unit
}

case class MyClass(text: String)

implicit val myClassTCInst: MyTypeclass[MyClass] = new MyTypeclass[MyClass] {
  override def display(myCustomClass: MyClass): Unit = println(myCustomClass.text)
}

def display[A](a: A)(implicit myTypeclass: MyTypeclass[A]): Unit = myTypeclass.display(a)
