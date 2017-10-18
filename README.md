# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>
<br/>
We continue by separating our business model directly from the Json language. 

## Example 2 - Separate the Business Model

In the first example, we inherited directly from the Json class, however our business language isn't Json.
What we really want is to convert all of our models to Json, then we can stringify them.

### Library Design

We now seal our abstract class, so no one else can inherit from it.
```scala
sealed abstract class Json {
  def stringify: String
}
```

We now introduce a way to convert to Json.
```scala
trait JsonConvertable {
  def toJson: Json
}
```

### Business Model

Instead of extending from Json we inherit from JsonConvertible

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends JsonConvertable {
  override def toJson =
    if (nickName.isEmpty) JsonObject(
      "name" -> JsonString(name),
      "age" -> JsonNumber(age),
      "alive" -> JsonBoolean(alive)
    ) else JsonObject(
      "name" -> JsonString(name),
      "age" -> JsonNumber(age),
      "alive" -> JsonBoolean(alive),
      "nickname" -> JsonString(nickName.get)
    )
}
```

**Family**
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person]) extends JsonConvertable {
  override def toJson = JsonObject(
    "surName" -> JsonString(surName),
    "mother" -> mother.toJson,
    "father" -> father.toJson,
    "children" -> JsonArray(children.map(_.toJson): _*)
  )
}
```

* Note we have converted everything to case classes so we no longer need to say `new` 

### Usage

We now have to call `.toJson` on our model and then `.stringify`

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