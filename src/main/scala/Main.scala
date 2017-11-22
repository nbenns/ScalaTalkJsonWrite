import models._
import json._
import json.models._
import shapeless._
import shapeless.labelled._
import shapeless.syntax.singleton._
import shapeless.labelled.{FieldType, KeyTag}
import shapeless.syntax.SingletonOps

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

  type Name = Witness.`'name`.T
  type Age = Witness.`'age`.T
  type Alive = Witness.`'alive`.T
  type NickName = Witness.`'nickName`.T

  val test: FieldType[Name, String] :: FieldType[Age, Int] :: FieldType[Alive, Boolean] :: FieldType[NickName, Option[String]] :: HNil =
    field[Name]("Homer") :: field[Age](37) :: field[Alive](true) :: field[NickName](Some("Mr. Sparkle")) :: HNil

  val testJson: JsonObject = test.toJson

  println(testJson.stringify)
}
