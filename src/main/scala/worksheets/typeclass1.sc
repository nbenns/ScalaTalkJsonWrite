
trait Displayable[A] {
  def display(a: A): Unit
}

case class MyClass(text: String)

def display[A](a: A, myTypeclass: Displayable[A]): Unit = myTypeclass.display(a)


val myClass = MyClass("hello")

val myClassTCInst: Displayable[MyClass] = new Displayable[MyClass] {
  override def display(myCustomClass: MyClass): Unit = println(myCustomClass.text)
}

display(myClass, myClassTCInst)