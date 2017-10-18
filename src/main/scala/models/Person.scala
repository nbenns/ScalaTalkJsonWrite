package models

import json.JsonConvertable
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends JsonConvertable {
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
