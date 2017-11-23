type File = (String, Int)

def getFileName(f: File): String = f._1
def getSize(f: File): Int = f._2

val file1 = ("File1", 1024)

val file1Name = getFileName(file1)
val file1Size = getSize(file1)