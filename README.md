# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

We have one more thing to fix, Family isn't working, so let's fix it and bring this to a close.

## Example 13 - Lazy Type Evaluation

Did you pick up on the cop out in the last example.
Why are we only converting a Person and not a Family?

If you tried it, you would know its not working and you get a cryptic error
```sbtshell
diverging implicit expansion for type json.JsonConvertible.Aux[Int with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("age")],Int] :: Boolean with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("alive")],Boolean] :: Option[String] with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("nickName")],Option[String]] :: shapeless.HNil,json.models.JsonObject]
[error] starting with method GenericToJson in package json
[error]   println(simpsons.toJson.stringify)
```

What is diverging implicit expansion?

Let's do some substitution and see:

We start with a Family, how does it figure out how to convert `Family`? It uses Generic.
Now we have this:

`String :: Person :: Person :: List[Person] :: HNil`

So it now calls HCons to figure out how to convert the HList.

Then we end up with the head `String` which it can just call then String conversion on, and then recurses into HCons again for the tail.
When we get to the second HCons call this is where we start to have problems. We now have a `Person` for the head.

How does it convert Person?  Well it calls Generic again. Now person gets "expanded" to:

`String :: Int :: Boolean :: Option[String] :: HNil`

And this expansion is where our problem lies... scalac doesn't like the fact that our type just suddenly ballooned up in the middle of the conversion process and it freeks out.
Scala is trying to be safe and prevent an infinite compilation loop that will steal all your cpu and suck your laptop's battery dry.

So what do we do about this? Get Lazy! 
```scala
implicit def HConsToJson[K <: Symbol, H, T <: HList]
(implicit
  headConv: Lazy[JsonConvertible[H]],
  tailConv: Lazy[JsonConvertible.Aux[T, JsonObject]],
  key: Witness.Aux[K]
) = new JsonConvertible[FieldType[K, H] :: T] {
  override type Enc = JsonObject
  override def toJson(a: FieldType[K, H] :: T) = {
    val headJson = headConv.value.toJson(a.head)
    val tailJson = tailConv.value.toJson(a.tail)

    tailJson + (key.value.name -> headJson)
  }
}
```

All we did here was wrap our converters in `Lazy` and then call `.value` on them when we use them.
That's it!

Just to close everything off lets do one more thing, get rid of the pesky nulls from our Json output.

We'll just match on the output type of headJson and if its JsonNull we'll just return the tail (don't add it to the object).

```scala
implicit def HConsToJson[K <: Symbol, H, T <: HList]
(implicit
  headConv: Lazy[JsonConvertible[H]],
  tailConv: Lazy[JsonConvertible.Aux[T, JsonObject]],
  key: Witness.Aux[K]
) = new JsonConvertible[FieldType[K, H] :: T] {
  override type Enc = JsonObject
  override def toJson(a: FieldType[K, H] :: T) = {
    val headJson = headConv.value.toJson(a.head)
    val tailJson = tailConv.value.toJson(a.tail)

    headJson match {
      case _: JsonNull => tailJson
      case _ => tailJson + (key.value.name -> headJson)
    }
  }
}
```


### Business Model

No change to the model


### Usage
Finally, we can output Family again

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
{"surName": "Simpson", "mother": {"name": "Marge", "age": 34, "alive": true}, "father": {"name": "Homer", "age": 37, "alive": true, "nickName": "Mr. Sparkle"}, "children": [{"name": "Bart", "age": 10, "alive": true, "nickName": "El Barto"}, {"name": "Lisa", "age": 8, "alive": true}, {"name": "Maggie", "age": 1, "alive": true}]}
```