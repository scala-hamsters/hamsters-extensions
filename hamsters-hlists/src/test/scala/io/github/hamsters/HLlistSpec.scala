import io.github.hamsters.{HList, HCons, HNil, HListExtensions}
import HListExtensions._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer

package io.github.hamsters

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer

class HListSpec extends FlatSpec with Matchers {

  "HList cons" should "construct from elements" in {

    ("hi" :: HNil).tail shouldBe HNil
    ("hi" :: HNil).head shouldBe "hi"

    val hlist = true :: 2.0 :: "hi" :: HNil
    //type erasure
    hlist shouldBe a[HCons[_, HCons[_, HNil]]]
    hlist.tail shouldBe a[HCons[_, HNil]]

    hlist.head shouldBe a[java.lang.Boolean]
    hlist.tail.head shouldBe a[java.lang.Double]

  }


  "HList ++ " should "append hlist to another one" in {

    import HList._

    val hlist1 = 2.0 :: "hi" :: HNil
    val hlist2 = 1 :: HNil

    val sum = hlist1 ++ hlist2

    sum shouldBe 2.0 :: "hi" :: 1 :: HNil
    sum shouldBe a[HCons[_, HCons[_, HCons[_, HNil]]]]
    sum.head shouldBe a[java.lang.Double]
    sum.head shouldBe 2.0
    sum.tail.head shouldBe "hi"
    sum.tail.tail.head shouldBe 1
  }

  "HList + " should "append element to a hlist " in {

    val hlist = 2.0 :: "hi" :: HNil
    val sum = hlist + 1
    sum shouldBe 2.0 :: "hi" :: 1 :: HNil
    sum shouldBe a[HCons[_, HCons[_, HCons[_, HNil]]]]
    sum.head shouldBe a[java.lang.Double]
    sum.head shouldBe 2.0
    sum.tail.head shouldBe "hi"
    sum.tail.tail.head shouldBe 1

  }

  "HList get" should "retrieve element with type and index" in {

    val hlist = 2.0 :: "hi" :: HNil
    hlist.get[String](1) should be(Some("hi"))
    hlist[String](1) should be("hi")

  }


  "HList get" should "give None at wrong index" in {

    val hlist = 2.0 :: "hi" :: HNil
    hlist.get[String](2) should be(None)

  }

  "HList get" should "give None at wrong type" in {

    val hlist = 2.0 :: "hi" :: HNil
    hlist.get[String](0) should be(None)

  }


  "HList fold" should "old over elements and produce a result" in {

    (2 :: "hi" :: HNil).foldLeft("")(_ + _) shouldBe "2hi"

  }


  "HList map" should "map elements" in {

    (2 :: "hi" :: HNil).map(_.toString) shouldBe ("2" :: "hi" :: HNil)

  }

  "HList filter" should "keep only elements satisfying predicate" in {

    //first element : false
    (2.0 :: "hi" :: "bye" :: HNil).filter {
      case s: String if s.startsWith("h") => true
      case _ => false
    } shouldBe ("hi" :: HNil)


    //first element : true
    ("hi" :: 2.0 :: "bye" :: HNil).filter {
      case s: String if s.startsWith("h") => true
      case _ => false
    } shouldBe ("hi" :: HNil)

    //all true
    ("hi" :: "helicopter" :: HNil).filter {
      case s: String if s.startsWith("h") => true
      case _ => false
    } shouldBe ("hi" :: "helicopter" :: HNil)

    //all false
    ("ih" :: 2.0 :: HNil).filter {
      case s: String if s.startsWith("h") => true
      case _ => false
    } shouldBe HNil


  }

  "HList foreach" should "iterate over elements" in {

    val list = ListBuffer[String]()
    (2.0 :: "hi" :: "hello" :: HNil).foreach {
      case s: String => list += s
      case _ =>
    }

    list shouldBe List("hi", "hello")

  }

  "HList" should "chain map filter and foreach" in {

    val list = ListBuffer[String]()
    val hlist = 2.0 :: "bye" :: "hi" :: "hello" :: HNil

    hlist.map(_.toString).filter {
      case s: String if s.startsWith("h") => true
      case _ => false
    }.foreach {
      case s: String => list += s
      case _ =>
    }

    list shouldBe List("hi", "hello")

  }

  "HList ::" should "val hList : String :: Boolean :: HNil should compile" in {

    import HList._

    val hList : String :: Boolean :: HNil = "hello"::true::HNil

    hList.head shouldBe a[java.lang.String]
    hList.get[Boolean](1) shouldBe Some(true)

  }

  "HList select[U]" should "extract a value of  type U" in {
    val hlist = 1 :: true :: "foo" :: 2.0 :: "bar" :: HNil
    hlist.select[Int] shouldBe 1
    hlist.select[Boolean] shouldBe true
    hlist.select[String] shouldBe "foo"
    hlist.select[Double] shouldBe 2.0
    //hlist.select[Float] shouldBe 999 //doesn't compile
  }
}
