# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>
<br/>
It starts with a standard OOP design pattern and then uses some of Scala's functional design patterns to implement more levels of abstraction. 

## Example 1 - Initial OOP Design

This branch is the initial OOP design.

### Library Design

The JSON language is made up of some specific objects:
* Boolean
* Number
* String
* Array
* Object

Each Noun in our language can be "stringified", meaning it can be converted to a String which can be easily sent across the network.

We start with defining an abstract class to represent the language, with our `stringify` method to convert each of our Nouns into their String representation.
```scala
abstract class Json {
  def stringify: String
}
```

Then each of the Nouns within the JSON language are implemented as classes and inherit from our language base.

**Boolean**
```scala
class JsonBoolean(b: Boolean) extends Json {
  override def stringify = b.toString
}
```
**Number**
```scala
class JsonNumber(n: Double) extends Json {
  override def stringify =
    if (n % 1 == 0) n.toInt.toString
    else n.toString
}
```
**String**
```scala
class JsonString(s: String) extends Json {
  override def stringify = "\"" + s + "\""
}
```
**Array**
```scala
class JsonArray(elements: Json*) extends Json {
  override def stringify = "[" + elements.map(_.stringify).mkString(", ") + "]"
}
```
**Object**
```scala
class JsonObject(pairs: (String, Json)*) extends Json {
  private val stringifyPair = ((k: String, v: Json) => {
    val key = new JsonString(k).stringify
    val value = v.stringify

    s"$key: $value"
  }).tupled

  override def stringify = "{" + pairs.map(stringifyPair).mkString(", ") + "}"
}
```

### Business Model

The business model of the application is a Family.
A Family has a Name, a mother, a father, and children.

Each family member is represented as a Person, and a Person has a Name, an Age and is either Alive or Dead

<br/>

**Person**
```scala
class Person(name: String, age: Int, alive: Boolean)
```

**Family**
```scala
class Family(surName: String, mother: Person, father: Person, children: List[Person])
```

### Bringing it together

We want to represent our Business Model as JSON so we can send it across the internet.
To do this we extend our models from JSON and implement the `stringify` method.

**Person**
```scala
class Person(name: String, age: Int, alive: Boolean, nickName: Option[String]) extends Json {
  override def stringify =
    if (nickName.isEmpty) new JsonObject(
      "name" -> new JsonString(name),
      "age" -> new JsonNumber(age),
      "alive" -> new JsonBoolean(alive)
    ).stringify else new JsonObject(
      "name" -> new JsonString(name),
      "age" -> new JsonNumber(age),
      "alive" -> new JsonBoolean(alive),
      "nickname" -> new JsonString(nickName.get)
    ).stringify
}
```

**Family**
```scala
class Family(surName: String, mother: Person, father: Person, children: List[Person]) extends Json {
  override def stringify = new JsonObject(
    "surName" -> new JsonString(surName),
    "mother" -> mother,
    "father" -> father,
    "children" -> new JsonArray(children: _*)
  ).stringify
}
```

Now let's put it all together.
```scala
val homer = new Person(name = "Homer", age = 37, alive = true, nickName = Some("Mr. Sparkle"))
val marge = new Person(name = "Marge", age = 34, alive = true, nickName = None)
val bart = new Person(name = "Bart", age = 10, alive = true, nickName = Some("El Barto"))
val lisa = new Person(name = "Lisa", age = 8, alive = true, nickName = None)
val maggie = new Person(name = "Maggie", age = 1, alive = true, nickName = None)

val simpsons = new Family(
  surName = "Simpson",
  mother = marge,
  father = homer,
  children = List(bart, lisa, maggie)
)

println(simpsons.stringify)
```

**Output**
```json
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true}, "father": {"name": "Homer", "age": 37, "alive": true, "nickname": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickname": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true}, {"name": "Maggie", "age": 1, "alive": true}]}
```