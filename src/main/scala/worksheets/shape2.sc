type File = (String, Int)

implicit class FileOps(f: File) {
  def fileName: String = f._1
  def size: Int = f._2
}

object File {
  def apply(name: String, size: Int): File = (name, size)
}

val file1 = File("File1", 1024)

val file1Name = file1.fileName
val file1Size = file1.size