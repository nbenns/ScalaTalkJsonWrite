import models._
import json._
import json.models._
import shapeless._

object Main extends App {
  val homer = Person(name = "Homer", age = 37, alive = true, nickName = Some("Mr. Sparkle"))
  val marge = Person(name = "Marge", age = 34, alive = true, nickName = None)
  val bart = Person(name = "Bart", age = 10, alive = true, nickName = Some("El Barto"))
  val lisa = Person(name = "Lisa", age = 8, alive = true, nickName = None)
  val maggie = Person(name = "Maggie", age = 1, alive = true, nickName = None)

  val simpsons = Family(
    surName = "Simpson",
    mother = marge,
    father = homer,
    children = List(bart, lisa, maggie)
  )

  val test: String :: Int :: Boolean :: Option[String] :: HNil =
    "Homer" :: 37 :: true :: Some("Mr. Sparkle") :: HNil

  val testJson: JsonObject = test.toJson

  println(testJson.stringify)
}
