type File = (String, Int)

implicit class FileOps(f: File) {
  def fileName: String = f._1
  def size: Int = f._2
}

val file1 = ("File1", 1024)

/*
  We have to deal with an entire tuple at once
  Also, what size tuple?  there are 22 tuples + Unit for zero
 */

file1 match {
  case (name, size) => println(s"Name: $name, Size: $size")
}

/*
  It would be better to deal with our classes like a list
 */
def join[A](lst: List[A]): String = lst match {
  case (h :: t) => h.toString + ", " + join(t)
  case Nil => "..."
}

val numList = List(1, 2, 3)
join(numList)
