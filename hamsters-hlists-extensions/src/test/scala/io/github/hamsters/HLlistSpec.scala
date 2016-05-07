import io.github.hamsters.{HList, HCons, HNil, HListExtensions}
import HListExtensions._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ListBuffer

class HListSpec extends FlatSpec with Matchers {

  "HList select[U]" should "extract a value of  type U" in {
    val hlist = 1 :: true :: "foo" :: 2.0 :: "bar" :: HNil
    hlist.select[Int] shouldBe 1
    hlist.select[Boolean] shouldBe true
    hlist.select[String] shouldBe "foo"
    hlist.select[Double] shouldBe 2.0
    //hlist.select[Float] shouldBe 999 //doesn't compile
  }
}
