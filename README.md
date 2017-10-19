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

## Example 5 - Get rid of JsonObject and any reference of Json within our models

Now we have abstracted over every type we are using in our models, but can we abstract over the models themselves?

### Library design

We can't do this alone, we need to pull something in to be able to get this kind of power.
The only hope we have is in `Reflection`, which will let us inspect our `Product` types at runtime and iterate over their methods

Add the reflection library
```sbtshell
libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value
)
```

Now use some reflection to implement the ultimate extension method

```scala
implicit class ProductToJson(o: Product) extends JsonConvertible {
  private def cast[T](a: Any, tt: TypeTag[T]): T = a.asInstanceOf[T]

  private def convert[T: TypeTag](a: Any): Json = a match {
    case value: Boolean => value.toJson
    case value: Int => value.toJson
    case value: Double => value.toJson
    case value: String => value.toJson
    case value: Seq[Any] => value.map(convert).toJson
    case value: Option[Any] => value.map(convert).toJson
    case value if typeOf[T] <:< typeOf[Product] =>
      val prod: Product = cast(value, implicitly[TypeTag[Product]])
      ProductToJson(prod).toJson
    case _ => throw new Exception("type not supported")
  }

  private def toMethods[T: TypeTag](c: Product): Seq[(String, Json)] = {
    val keys = c.getClass.getDeclaredFields.map(_.getName)
    val z = (keys zip c.productIterator.toList) map {
      case (key, value) => (key, convert(value))
    }

    z
  }

  val methods = toMethods(o)

  override def toJson = JsonObject(methods: _*)
}
``` 

### Business Model

We can remove everything but our case classes!

**Person**
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])
```

**Family**
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person])
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
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true, "nickname": null}, "father": {"name": "Homer", "age": 37, "alive": true, "nickname": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickname": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true, "nickname": null}, {"name": "Maggie", "age": 1, "alive": true, "nickname": null}]}
```