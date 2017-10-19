package models

import json._
import json.models._

case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit val FamilyToJson: JsonConvertible[Family] = f => JsonObject(
    "surName" -> toJson(f.surName),
    "mother" -> toJson(f.mother),
    "father" -> toJson(f.father),
    "children" -> toJson(f.children)
  )
}
