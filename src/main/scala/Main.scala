import models._

object Main extends App {
  val homer = new Person(name = "Homer", age = 37, alive = true)
  val marge = new Person(name = "Marge", age = 34, alive = true)
  val bart = new Person(name = "Bart", age = 10, alive = true)
  val lisa = new Person(name = "Lisa", age = 8, alive = true)
  val maggie = new Person(name = "Maggie", age = 1, alive = true)

  val simpsons = new Family(
    surName = "Simpson",
    mother = marge,
    father = homer,
    children = List(bart, lisa, maggie)
  )

  println(simpsons.stringify)
}
