package models

import json.JsonConvertable
import json.models._

case class Family(surName: String, mother: Person, father: Person, children: List[Person]) extends JsonConvertable {
  override def toJson = JsonObject(
    "surName" -> JsonString(surName),
    "mother" -> mother.toJson,
    "father" -> father.toJson,
    "children" -> JsonArray(children.map(_.toJson): _*)
  )
}
