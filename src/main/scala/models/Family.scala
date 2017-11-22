package models

import json._
import json.models._

case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit val FamilyToJson = new JsonConvertible[Family] {
    override type Enc = JsonObject

    override def toJson(f: Family) = JsonObject(
      "surName" -> f.surName.toJson,
      "mother" -> f.mother.toJson,
      "father" -> f.father.toJson,
      "children" -> f.children.toJson
    )
  }
}
