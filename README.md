# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>
<br/>
We take our new extension method pattern and use it for our model.  

## Example 4 - Switch our Model to Extension Methods

In the 3rd example, we used extension methods on primitives to give them a `.toJson` method.
Here we try to implement the same pattern on our model to separate the conversion from our business logic.

### Library design

Since we want to remove the inheritance of the JsonConvertible from our Models, we need to make one change in one of our implicit classes.
The SeqToJson class requires that each element extends JsonConvertible, but now all we will be able to do is require that they are Json instead.
This means we lose some power, but all we need to do is convert the elements first and then the list itself, so its not that big of a deal.

```scala
implicit class SeqToJson[A <: Json](l: Seq[A]) extends JsonConvertible {
  override def toJson = JsonArray(l: _*)
}
``` 

### Business Model

Extension methods for our types

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit class PersonToJson(p: Person) extends JsonConvertible {
    override def toJson =
      if (p.nickName.isEmpty) JsonObject(
        "name" -> p.name.toJson,
        "age" -> p.age.toJson,
        "alive" -> p.alive.toJson
      ) else JsonObject(
        "name" -> p.name.toJson,
        "age" -> p.age.toJson,
        "alive" -> p.alive.toJson,
        "nickname" -> p.nickName.get.toJson
      )
  }
}
```

We are able to implement the extension method for Person, and putting it inside the companion object saves us an import as it will be found on implicit search.

**Family**
```scala
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
```

### Usage

No change to our usage

```scala
val homer = Person(name = "Homer", age = 37, alive = true, nickName = Some("Mr. Sparkle"))
val marge = Person(name = "Marge", age = 34, alive = true, nickName = None)
val bart = Person(name = "Bart", age = 10, alive = true, nickName = Some("El Barto"))
val lisa = Person(name = "Lisa", age = 8, alive = true, nickName = None)
val maggie = Person(name = "Maggie", age = 1, alive = true, nickName = None)

val simpsons = Family(
  surName = "Simpson",
  mother = marge,
  father = homer,
  children = List(bart, lisa, maggie)
)

println(simpsons.toJson.stringify)
```

**Output**
```json
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true}, "father": {"name": "Homer", "age": 37, "alive": true, "nickname": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickname": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true}, {"name": "Maggie", "age": 1, "alive": true}]}
```