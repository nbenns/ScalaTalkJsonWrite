import shapeless._

case class File(name: String, size: Int)

type GenericFile = String :: Int :: HNil

val genFile = implicitly[Generic.Aux[File, GenericFile]]

val file1 = File("File1", 1024)

val genFile1: GenericFile = genFile.to(file1)
