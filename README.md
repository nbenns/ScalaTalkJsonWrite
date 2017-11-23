# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>
<br/>
Lets take a step back and try another approach

## Example 6 - The Typeclass Pattern

Extension methods only got us so far.  A lot of the problem is that we can't specifically pull them in for each type, which locked us in with reflection.
Here we take a step back and change our JsonConverter to take a type parameter and implement the [TypeClass Pattern](http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html).

### Library design

Change our converter to a typeclass
```scala
trait JsonConvertible[A] {
  def toJson(a: A): Json
}
```

Now change all our extension methods to typeclass instances

**Boolean**
```scala
implicit val BooleanToJson: JsonConvertible[Boolean] = b => JsonBoolean(b)
``` 

**Int**
```scala
implicit val IntToJson: JsonConvertible[Int] = i => JsonNumber(i)
```

**Double**
```scala
implicit val DoubleToJson: JsonConvertible[Double] = d => JsonNumber(d)
```

**String**
```scala
implicit val StringToJson: JsonConvertible[String] = s => JsonString(s)
```

**Array**
```scala
implicit def SeqToJson[A: JsonConvertible, S[B] <: Seq[B]]: JsonConvertible[S[A]] = l => JsonArray(l.map(toJson[A]): _*)
```

**Option**
```scala
implicit def OptionToJson[A: JsonConvertible]: JsonConvertible[Option[A]] = {
  case Some(a) => toJson(a)
  case None => JsonNull
}
```

And add a function interface function for the conversion
```scala
def toJson[A](a: A)(implicit converter: JsonConvertible[A]): Json = converter.toJson(a)
```

### Business Model

We will need typeclass instances for each of our objects

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit val PersonToJson: JsonConvertible[Person] = p => JsonObject(
    "name" -> toJson(p.name),
    "age" -> toJson(p.age),
    "alive" -> toJson(p.alive),
    "nickName" -> toJson(p.nickName)
  )
}
```

**Family**
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit val FamilyToJson: JsonConvertible[Family] = f => JsonObject(
    "surName" -> toJson(f.surName),
    "mother" -> toJson(f.mother),
    "father" -> toJson(f.father),
    "children" -> toJson(f.children)
  )
}
```

### Usage

We just need to use our function as we don't have a method on our models anymore

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

println(toJson(simpsons).stringify)
```

**Output**

```json
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true, "nickname": null}, "father": {"name": "Homer", "age": 37, "alive": true, "nickname": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickname": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true, "nickname": null}, {"name": "Maggie", "age": 1, "alive": true, "nickname": null}]}
```