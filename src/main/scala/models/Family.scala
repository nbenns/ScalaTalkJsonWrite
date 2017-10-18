package models

import json._

class Family(
 surName: String,
 mother: Person,
 father: Person,
 children: List[Person]
) extends Json {
  override def stringify = new JsonObject(
    "surName" -> new JsonString(surName),
    "mother" -> mother,
    "father" -> father,
    "children" -> new JsonArray(children: _*)
  ).stringify
}
