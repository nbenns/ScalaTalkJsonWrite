
trait MyTypeclass[A] {
  def display(a: A): Unit
}

case class MyClass(text: String)

val myClassTCInst: MyTypeclass[MyClass] = new MyTypeclass[MyClass] {
  override def display(myCustomClass: MyClass): Unit = println(myCustomClass.text)
}

def display[A](a: A, myTypeclass: MyTypeclass[A]): Unit = myTypeclass.display(a)
