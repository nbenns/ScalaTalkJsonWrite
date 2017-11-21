package models

import json._

class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends Json {
  override def stringify =
    if (nickName.isEmpty) new JsonObject(
      "name" -> new JsonString(name),
      "age" -> new JsonNumber(age),
      "alive" -> new JsonBoolean(alive)
    ).stringify else new JsonObject(
      "name" -> new JsonString(name),
      "age" -> new JsonNumber(age),
      "alive" -> new JsonBoolean(alive),
      "nickname" -> new JsonString(nickName.get)
    ).stringify
}
