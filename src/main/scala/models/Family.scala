package models

import json._
import json.models._

case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit class FamilyToJson(f: Family) extends JsonConvertible {
    override def toJson = JsonObject(
      "surName" -> f.surName.toJson,
      "mother" -> f.mother.toJson,
      "father" -> f.father.toJson,
      "children" -> f.children.map(_.toJson).toJson
    )
  }
}
