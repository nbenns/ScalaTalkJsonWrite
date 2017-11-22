# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

## Example 8 - Begin Generic abstraction

We start implementing an HList implementation in order to abstract over the shape of our models.

### Library design

We need a way to build up a JsonObject piece by piece, so we add a method to do so
```scala
case class JsonObject(pairs: (String, Json)*) extends Json {
  private val stringifyPair = ((k: String, v: Json) => {
    val key = JsonString(k).stringify
    val value = v.stringify

    s"$key: $value"
  }).tupled

  override def stringify = "{" + pairs.map(stringifyPair).mkString(", ") + "}"

  def +(pair: (String, Json)): JsonObject = {
    val nPairs: List[(String, Json)] = pair :: pairs.toList
    JsonObject(nPairs: _*)
  }
}
```

Now we implement the JsonConversion class for HNil and HCons
```scala
implicit val HNilToJson: JsonConvertible[HNil] = _ => JsonObject()

implicit def HConsToJson[H: JsonConvertible, T <: HList : JsonConvertible]: JsonConvertible[H :: T] = { hlist =>
  val headJson = hlist.head.toJson
  val tailJson = hlist.tail.toJson.asInstanceOf[JsonObject]

  tailJson + ("key" -> headJson)
}
```

At this moment we don't have the ability to get our key value, we will have to revisit this later.

### Business Model

We aren't touching this for now.

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