package models

import json._
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit val PersonToJson = new JsonConvertible[Person] {
    override type Enc = JsonObject
    override def toJson(p: Person) = JsonObject(
      "name" -> p.name.toJson,
      "age" -> p.age.toJson,
      "alive" -> p.alive.toJson,
      "nickName" -> p.nickName.toJson
    )
  }
}
