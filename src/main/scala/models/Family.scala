package models

import json._
import json.models._

case class Family(surName: String, mother: Person, father: Person, children: List[Person]) extends JsonConvertible {
  override def toJson = JsonObject(
    "surName" -> surName.toJson,
    "mother" -> mother.toJson,
    "father" -> father.toJson,
    "children" -> children.toJson
  )
}
