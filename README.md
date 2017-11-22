# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

Here we are getting rid of the ugly typecast to JsonObject in our HCons implementation.

## Example 9 - Path Dependent Type

The reason we need the cast to JsonObject, is that our JsonConvertible's toJson method only guarentees that we are returning a type Json.
We have no idea which actual Json type we are returning.

What we are going to have to do is keep track of what type we are returning, and this will cause a lot of changes to our typeclass implementations, but it will be worth it.  

### Library design

We add a type alias to our JsonConvertible's interface that must be filled out in order to use our typeclass.
```scala
trait JsonConvertible[A] {
  type Enc <: Json
  def toJson(a: A): Enc
}
```

This ends up affecting all of our instances of course, and we can no longer use the concise Single Abstract Method syntax unfortunately.

#### Boolean
```scala
implicit val BooleanToJson = new JsonConvertible[Boolean] {
  override type Enc = JsonBoolean
  override def toJson(a: Boolean) = JsonBoolean(a)
}
```

#### Int
```scala
implicit val IntToJson = new JsonConvertible[Int] {
  override type Enc = JsonNumber
  override def toJson(a: Int) = JsonNumber(a)
}
```

#### Double
```scala
implicit val DoubleToJson = new JsonConvertible[Double] {
  override type Enc = JsonNumber
  override def toJson(a: Double) = JsonNumber(a)
}
```

#### String
```scala
implicit val StringToJson = new JsonConvertible[String] {
  override type Enc = JsonString
  override def toJson(a: String) = JsonString(a)
}
```

#### Sequence
```scala
implicit def SeqToJson[A: JsonConvertible, S[B] <: Seq[B]] = new JsonConvertible[S[A]] {
  override type Enc = JsonArray
  override def toJson(a: S[A]) = JsonArray(a.map(e => json.toJson(e)): _*)
}
```

#### Option
```scala
implicit def OptionToJson[A: JsonConvertible, B <: Json] = new JsonConvertible[Option[A]] {
  override type Enc = Json
  override def toJson(a: Option[A]) = a match {
    case Some(b) => json.toJson(b)
    case None => JsonNull
  }
}
```

### HList
```scala
implicit val HNilToJson = new JsonConvertible[HNil] {
  override type Enc = JsonObject
  override def toJson(a: HNil) = JsonObject()
}

implicit def HConsToJson[H, T <: HList]
(implicit
  headConv: JsonConvertible[H],
  tailConv: JsonConvertible[T]
) = new JsonConvertible[H :: T] {
  override type Enc = JsonObject
  override def toJson(a: ::[H, T]) = {
    val headJson = headConv.toJson(a.head)
    val tailJson = tailConv.toJson(a.tail).asInstanceOf[JsonObject]

    tailJson + ("key" -> headJson)
  }
}
```

Wait! What is going on here.  Didn't I promise that we would be able to get rid of that ugly cast?
Well yes, but there is one more step, and since this is such as huge change to our library, I broke it up.

This change also affects our interface function.  Notice the return type is now based on the conversion instance.
Since we need to access that now, we can no longer use the nice `[A : JsonConvertible]` syntax
```scala
def toJson[A](a: A)(implicit conv: JsonConvertible[A]): conv.Enc = conv.toJson(a)
```

It then affects our extension method as well.  We need to add val to our implicit parameter or Scala will complain we are bleeding the scope of a private variable.
```scala
implicit class JsonConversion[A](a: A)(implicit val conv: JsonConvertible[A]) {
  def toJson: conv.Enc = conv.toJson(a)
}
```

### Business Model

The same typeclass changes affect our business model as well.

#### Person
```scala
case class Person(name: String, age: Int, alive: Boolean, nickName: Option[String])

object Person {
  implicit val PersonToJson = new JsonConvertible[Person] {
    override type Enc = JsonObject
    override def toJson(p: Person) = JsonObject(
      "name" -> p.name.toJson,
      "age" -> p.age.toJson,
      "alive" -> p.alive.toJson,
      "nickName" -> p.nickName.toJson
    )
  }
}
```

#### Family
```scala
case class Family(surName: String, mother: Person, father: Person, children: List[Person])

object Family {
  implicit val FamilyToJson = new JsonConvertible[Family] {
    override type Enc = JsonObject

    override def toJson(f: Family) = JsonObject(
      "surName" -> f.surName.toJson,
      "mother" -> f.mother.toJson,
      "father" -> f.father.toJson,
      "children" -> f.children.toJson
    )
  }
}
```

### Usage

We are ignoring our actual business usage for now and focusing on just the HList.  We implement just the Generic version of Person to give it a try.
```scala
val testJson: String :: Int :: Boolean :: Option[String] :: HNil =
    "Homer" :: 37 :: true :: Some("Mr. Sparkle") :: HNil

println(testJson.toJson.stringify)
```

**Output**

```json
{"key": "Homer", "key": 37, "key": true, "key": "Mr. Sparkle"}
```