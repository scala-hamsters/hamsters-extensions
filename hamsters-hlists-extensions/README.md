# Hamsters Extentions - HList extra operations

## Select[U] feature by Damien Gouyette

Selects the first element of the requested type.

```scala
val hlist = 1 :: true :: "foo" :: 2.0 :: "bar" :: HNil
hlist.select[Int] shouldBe 1
hlist.select[Boolean] shouldBe true
hlist.select[String] shouldBe "foo"
hlist.select[Double] shouldBe 2.0
```
