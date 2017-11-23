trait MyTypeclass[A] {
  def display(a: A): Unit
}

case class MyClass(text: String)

def display[A](a: A)(implicit myTypeclass: MyTypeclass[A]): Unit = myTypeclass.display(a)


val myClass = MyClass("hello")

implicit val myClassTCInst: MyTypeclass[MyClass] = myCustomClass => println(myCustomClass.text)

display(myClass)