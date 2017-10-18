package models

import json.JsonConvertible
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends JsonConvertible {
  override def toJson =
    if (nickName.isEmpty) JsonObject(
      "name" -> JsonString(name),
      "age" -> JsonNumber(age),
      "alive" -> JsonBoolean(alive)
    ) else JsonObject(
      "name" -> JsonString(name),
      "age" -> JsonNumber(age),
      "alive" -> JsonBoolean(alive),
      "nickname" -> JsonString(nickName.get)
    )
}
