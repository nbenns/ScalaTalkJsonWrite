
implicit class AOps(str: String) {
  def display(): Unit = println("displaying: " + str)
}

val my_string = "hello"
my_string.display()

