# HList

HLists can contain heterogeneous data types but are strongly typed. It's like tuples on steroids!
When you're manipulating data using tuples, it's common to add or subtrack some elements, but you have to make each element explicit to build a new tuple. HList simplifies this kind of task.

 * `::` is used to append elements at the beginning of an HList, and to construct new HLists
 * `+` is used add element at the end of a HList
 * `++` is used to concatenate 2 Hlists
 * HNil is the empty HList
 * other operations: filter, map, foldLeft, foreach

```scala
import io.github.hamsters.{HList, HCons, HNil}
import HList._

val hlist1: Double :: String :: HNil = 2.0 :: "hi" :: HNil
val hlist2 = 1 :: HNil

val sum = hlist1 + 1 // 2.0 :: "hi" :: 1 :: HNil
val sum2 = hlist1 ++ hlist2 // 2.0 :: "hi" :: 1 :: HNil

sum2.tail // hi :: (1 :: HNil)
sum2.head // 2.0 (Double)
sum2.tail.head // "hi" (String)

// Retrieve element by index and type
hlist1.get[String](1) // Some("hi")
// Or use apply to avoid Option
hlist1[String](1) // "hi"

(2.0 :: "hi" :: HNil).foldLeft("")(_+_) // "2.0hi"

(2.0 :: "hi" :: HNil).map(_.toString) // "2.0" :: "hi" :: HNil

(2.0 :: "hi" :: HNil).filter {
  case s: String if s.startsWith("h") => true
  case _ => false
} // "hi" :: HNil

```

## HList <-> case class conversion macro

You can use the HList macro to do HList to case class and case class to HList conversions:

```scala
@HListMacro
case class Person(name: String, age: Int, weight: Option[Int] = None)

HList.toHList(Person(name = "Christophe Colomb", age = 42)) // "Christophe Colomb" :: 42 :: None :: HNil
HList.toClass[Person]("Christophe Colomb" :: 42 :: None :: HNil) // Person(name = "Christophe Colomb", age = 42)
```
### Depedencies 

To use the HList macro, you need to add this dependencies to your build: 

```scala
libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0" % Provided
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full)
```

## Select[U]

Selects the first element of the requested type.

```scala
val hlist = 1 :: true :: "foo" :: 2.0 :: "bar" :: HNil
hlist.select[Int] shouldBe 1
hlist.select[Boolean] shouldBe true
hlist.select[String] shouldBe "foo"
hlist.select[Double] shouldBe 2.0
```
