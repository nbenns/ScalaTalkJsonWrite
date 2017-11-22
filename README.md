# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

Let's finally abstract over the shape of our models.

## Example 12 - Generic Conversion

Now its time to reap the fruits of our labor.
We need to implement one more type class instance, the one that will convert between our case classes and the HList.

Here we need to lock our generic representation type to the same type as our JsonConvertible implementation, as well as locking the output of JsonConvertible to JsonObject.
```scala
implicit def GenericToJson[A, R]
(implicit
  gen: LabelledGeneric.Aux[A, R],
  conv: JsonConvertible.Aux[R, JsonObject]
) = new JsonConvertible[A] {
  override type Enc = JsonObject
  override def toJson(a: A) = conv.toJson(gen.to(a))
}
```

### Business Model

We can now rip out all of the case objects out of our models.

#### Person
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])
```

#### Family
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person])
```

### Usage
And lets get rid of all the test crud in our Main, and return to the old usage.
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

println(homer.toJson.stringify)
```

**Output**

```json
{"name": "Homer", "age": 37, "alive": true, "nickName": "Mr. Sparkle"}
```