package models

import json._

class Person(name: String, age: Int, alive: Boolean) extends Json {
  override def stringify = new JsonObject(
    "name" -> new JsonString(name),
    "age" -> new JsonNumber(age),
    "alive" -> new JsonBoolean(alive)
  ).stringify
}
