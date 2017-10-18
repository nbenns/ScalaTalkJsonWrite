package models

import json._
import json.models._

case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends JsonConvertible {
  override def toJson =
    if (nickName.isEmpty) JsonObject(
      "name" -> name.toJson,
      "age" -> age.toJson,
      "alive" -> alive.toJson
    ) else JsonObject(
      "name" -> name.toJson,
      "age" -> age.toJson,
      "alive" -> alive.toJson,
      "nickname" -> nickName.get.toJson
    )
}
