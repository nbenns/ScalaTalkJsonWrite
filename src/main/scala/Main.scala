import models._

object Main extends App {
  val homer = new Person(name = "Homer", age = 37, alive = true, nickName = Some("Mr. Sparkle"))
  val marge = new Person(name = "Marge", age = 34, alive = true, nickName = None)
  val bart = new Person(name = "Bart", age = 10, alive = true, nickName = Some("El Barto"))
  val lisa = new Person(name = "Lisa", age = 8, alive = true, nickName = None)
  val maggie = new Person(name = "Maggie", age = 1, alive = true, nickName = None)

  val simpsons = new Family(
    surName = "Simpson",
    mother = marge,
    father = homer,
    children = List(bart, lisa, maggie)
  )

  println(simpsons.stringify)
}
