package models

case class Family(surName: String, mother: Person, father: Person, children: List[Person])
