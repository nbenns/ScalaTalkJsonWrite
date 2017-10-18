# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>
<br/>
We continue by trying to fix the lopsidedness of the usage of the library  

## Example 3 - Same interface for Primitives

In the second example we implemented a method to convert to Json instead of extending it.
This time we want to treat primitives outside our model the same way (by calling `.toJson` on them). 

### Library Design

Inside our library we now add some [Extension Methods](https://sachabarbs.wordpress.com/2015/10/23/scala-extension-methods/) for the primatives we are using.

**Boolean**
```scala
implicit class BooleanToJson(b: Boolean) extends JsonConvertable {
  override def toJson = JsonBoolean(b)
}
```

**Number**
```scala
implicit class IntToJson(i: Int) extends JsonConvertable {
  override def toJson = JsonNumber(i)
}

implicit class DoubleToJson(d: Double) extends JsonConvertable {
  override def toJson = JsonNumber(d)
}
```

**String**
```scala
implicit class StringToJson(s: String) extends JsonConvertable {
  override def toJson = JsonString(s)
}
```

**Array**
```scala
implicit class SeqToJson[A <: JsonConvertable](l: Seq[A]) extends JsonConvertable {
  override def toJson = JsonArray(l.map(_.toJson): _*)
}
```
### Business Model

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends JsonConvertible {
  override def toJson =
    if (nickName.isEmpty) JsonObject(
      "name" -> name.toJson,
      "age" -> age.toJson,
      "alive" -> alive.toJson
    ) else JsonObject(
      "name" -> name.toJson,
      "age" -> age.toJson,
      "alive" -> alive.toJson,
      "nickname" -> nickName.get.toJson
    )
}
```

**Family**
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person]) extends JsonConvertible {
  override def toJson = JsonObject(
    "surName" -> surName.toJson,
    "mother" -> mother.toJson,
    "father" -> father.toJson,
    "children" -> children.toJson
  )
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