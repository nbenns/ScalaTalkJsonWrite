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

## Example 7 - One Extension Method for all TypeClass Instances

For user-friendliness and flexibility of expression we introduce an extension method implementation again.  However this time we only need one.

### Library design

Add our extension method for all JsonConvertible types
```scala
implicit class JsonConversion[A: JsonConvertible](a: A) {
  def toJson: Json = implicitly[JsonConvertible[A]].toJson(a)
}
```

Now we can simplify our function interface's signature

```scala
def toJson[A: JsonConvertible](a: A): Json = a.toJson
```

### Business Model

We can use `.toJson` if we like now instead of calling the interface function

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit val PersonToJson: JsonConvertible[Person] = p => JsonObject(
    "name" -> p.name.toJson,
    "age" -> p.age.toJson,
    "alive" -> p.alive.toJson,
    "nickName" -> p.nickName.toJson
  )
}
```

**Family**
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit val FamilyToJson: JsonConvertible[Family] = f => JsonObject(
    "surName" -> f.surName.toJson,
    "mother" -> f.mother.toJson,
    "father" -> f.father.toJson,
    "children" -> f.children.toJson
  )
}
```

### Usage

We can go back to calling `.toJson` again.

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
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true, "nickname": null}, "father": {"name": "Homer", "age": 37, "alive": true, "nickname": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickname": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true, "nickname": null}, {"name": "Maggie", "age": 1, "alive": true, "nickname": null}]}
```