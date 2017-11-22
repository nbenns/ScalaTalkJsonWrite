package models

import json._
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit class PersonToJson(p: Person) extends JsonConvertible {
    override def toJson = JsonObject(
      "name" -> p.name.toJson,
      "age" -> p.age.toJson,
      "alive" -> p.alive.toJson,
      "nickname" -> p.nickName.map(_.toJson).toJson
    )
  }
}
