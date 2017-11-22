# ScalaTalkJsonWrite

This project was created for a Scala Toronto Talk.
<br/>
Slides are located [here](https://docs.google.com/presentation/d/13akNnJCuATS0mqc5ULNG2jvaoFbCZ163OkImTfpxjbA/edit?usp=sharing)
<br/>
<br/>
The idea in this talk is to implement a JSON serializer library that we can use with the least amount of effort possible, and the least amount of bleed into our business design.
<br/>  

Time to fill in the names of our JsonObject's keys.

## Example 11 - Type Annotation

Maybe you have been wondering through the last few examples where our Keys were going to come from in our object?
It might have made more intuitive sense to use something like a Map instead of HList, but Map wouldn't have solved our problem.
It has the same restrictions as List where the values must all be of the same type.

Maybe you think we should have created an HMap instead of an HList, and such a thing exists, but to solve a host of different problems.

Really the list is the simplest thing to iterate over each of our types, but again, where does the key names come from?

What we are going to do next is strange.  It doesn't make any intuitive sense, but hopefully it won't scare you off.
We are getting down into the dark holes of shapeless, really because I want to show you how it works, but also because we need our HList's typeclass instances to handle this.

Its actually really not that complicated, but it looks cryptic and scary.
Anyway, here we go... 

### Library design

Our solution to this problem is to annotate the types of each of the fields with the Name of the field.
This might seem weird, and it is, but take a look at this first so you understand what I mean.
```scala
type Name = Witness.`'name`.T
val nameOfHomer: FieldType[Name, String] = field[Name]("Homer")
val nameWitness = implicitly[Witness.Aux[Name]]
println(nameWitness.value.name) // prints "name"
```

This looks like magic, and it is magic, magic made by Macros the Scala compiler is running to generate these weird types.
I'm not going to get into how these macros work, that's beyond the scope of this tutorial.

What I will say is this, we can use Shapless' Witness type to create a type from a symbol.
Then we can annotate a type like String with our new type by using the FieldType type constructor.

Those 4 lines open up a huge can of worms, and are probably the most confusing part of this whole tutorial, so its definitely worth the extra reading before you go on.
However they will be gone in the next example, we are only interested in simulating LabelledGeneric, so we are putting them in to make it work and then build upon.

Let's add all of our types for Person and create our HList for homer again.

```scala
type Name = Witness.`'name`.T
type Age = Witness.`'age`.T
type Alive = Witness.`'alive`.T
type NickName = Witness.`'nickName`.T

val test: FieldType[Name, String] :: FieldType[Age, Int] :: FieldType[Alive, Boolean] :: FieldType[NickName, Option[String]] :: HNil =
  field[Name]("Homer") :: field[Age](37) :: field[Alive](true) :: field[NickName](Some("Mr. Sparkle")) :: HNil
```

Now we need to implement this FieldType on our HCons implementation

```scala
implicit def HConsToJson[K <: Symbol, H, T <: HList]
(implicit
  headConv: JsonConvertible[H],
  tailConv: JsonConvertible.Aux[T, JsonObject],
  key: Witness.Aux[K]
) = new JsonConvertible[FieldType[K, H] :: T] {
  override type Enc = JsonObject
  override def toJson(a: FieldType[K, H] :: T) = {
    val headJson = headConv.toJson(a.head)
    val tailJson = tailConv.toJson(a.tail)

    tailJson + (key.value.name -> headJson)
  }
}
```
We add a type parameter S which is a Symbol represent the type annotation.
We then add key which pulls in the Witness for that key and will allow us to get the String name of the type.

We can then use it like normal
```scala
val testJson: JsonObject = test.toJson

println(testJson.stringify)
```

* Note: because there is a lot of macro magic going on here, IntelliJ or Eclipse might have problems following.  The code will compile however, even if the IDE doesn't think so.

### Business Model

No changes

### Usage

We are ignoring our actual business usage for now and focusing on just the HList.  We implement just the Generic version of Person to give it a try.
```scala
type Name = Witness.`'name`.T
type Age = Witness.`'age`.T
type Alive = Witness.`'alive`.T
type NickName = Witness.`'nickName`.T

val test: FieldType[Name, String] :: FieldType[Age, Int] :: FieldType[Alive, Boolean] :: FieldType[NickName, Option[String]] :: HNil =
  field[Name]("Homer") :: field[Age](37) :: field[Alive](true) :: field[NickName](Some("Mr. Sparkle")) :: HNil

val testJson: JsonObject = test.toJson

println(testJson.stringify)
```

**Output**

```json
{"name": "Homer", "age": 37, "alive": true, "nickName": "Mr. Sparkle"}
```