# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

Here we finally get rid of the ugly typecast to JsonObject in our HCons implementation.

## Example 9 - The Aux Pattern

Now that we are keeping track of our return type, why is the cast still required in HCons' implementation?
The problem is now that type return type is dependent on tailConv.Enc.  What we need to be able to do, is to restrict tailConv.Enc to JsonObject.

That's what we are going to do now.  And to do this we will need something called the [Aux Pattern](http://gigiigig.github.io/posts/2015/09/13/aux-pattern.html)

### Library design

We add a companion object to our JsonConvertible trait and in there put a type called Aux.
```scala
trait JsonConvertible[A] {
  type Enc <: Json
  def toJson(a: A): Enc
}

object JsonConvertible {
  type Aux[In, Out] = JsonConvertible[In] { type Enc = Out }
}
```

What this allows us to do is specify another Type Parameter to our methods that can restrict the Enc type on our typeclass.
Here is the new HList implementation:

```scala
implicit def HConsToJson[H, T <: HList]
(implicit
  headConv: JsonConvertible[H],
  tailConv: JsonConvertible.Aux[T, JsonObject]
) = new JsonConvertible[H :: T] {
  override type Enc = JsonObject
  override def toJson(a: ::[H, T]) = {
    val headJson = headConv.toJson(a.head)
    val tailJson = tailConv.toJson(a.tail)

    tailJson + ("key" -> headJson)
  }
}
```

Now the cast is completely gone!
We've restricted the tailConv instance to specifically return a JsonObject, so now tailConv.Enc is guarenteed to be JsonObject and we no longer have to cast.

We can also fix something nasty hiding in our extension methods as well.
There was no way to cast it as a specific Json type either, the only guarantee was again conv.Enc.
Now we can use Aux on our implicit class to fix that as well.

Just so you are confident what I'm talking about, let's modify our Main for a second and try a 2 step conversion.

```scala
val test: String :: Int :: Boolean :: Option[String] :: HNil =
    "Homer" :: 37 :: true :: Some("Mr. Sparkle") :: HNil

val testJson: JsonObject = toJson(test)

println(testJson.stringify)
``` 

This compiles no problem, and works as expected.
However if we try using `.toJson` instead
```scala
val test: String :: Int :: Boolean :: Option[String] :: HNil =
    "Homer" :: 37 :: true :: Some("Mr. Sparkle") :: HNil

val testJson: JsonObject = test.toJson

println(testJson.stringify)
```

We get a compilation failure
```sbtshell
[error] C:\ScalaTalkJsonWrite\src\main\scala\Main.scala:23: type mismatch;
[error]  found   : _1.conv.Enc where val _1: json.JsonConversion[String :: Int :: Boolean :: Option[String] :: shapeless.HNil]
[error]  required: json.models.JsonObject
[error]   val testJson: JsonObject = test.toJson
[error]                                   ^
[error] one error found
[error] (compile:compileIncremental) Compilation failed
[error] Total time: 2 s, completed Nov 22, 2017 12:09:55 PM
```

Using the Aux pattern on our implicit class will fix this as well.
```scala
implicit class JsonConversion[A, B <: Json](a: A)(implicit conv: JsonConvertible.Aux[A, B]) {
  def toJson: B = conv.toJson(a)
}
```

Now we can compile just fine.

### Business Model

No changes

### Usage

We are ignoring our actual business usage for now and focusing on just the HList.  We implement just the Generic version of Person to give it a try.
```scala
val test: String :: Int :: Boolean :: Option[String] :: HNil =
    "Homer" :: 37 :: true :: Some("Mr. Sparkle") :: HNil

val testJson: JsonObject = test.toJson

println(testJson.stringify)
```

**Output**

```json
{"key": "Homer", "key": 37, "key": true, "key": "Mr. Sparkle"}
```