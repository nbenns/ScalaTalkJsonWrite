package models

import json._
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit val PersonToJson: JsonConvertible[Person] = p => JsonObject(
    "name" -> toJson(p.name),
    "age" -> toJson(p.age),
    "alive" -> toJson(p.alive),
    "nickName" -> toJson(p.nickName)
  )
}
