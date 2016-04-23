package io.github.hamsters.twitter

import org.scalatest._
import com.twitter.{util => twitter}

import scala.concurrent._
import scala.concurrent.duration._
import Implicits._

import scala.util.{Failure, Success, Try}
/**
  * Created by ikhoon on 2016. 4. 23..
  */
class ImplicitsSpec extends FreeSpec with Matchers {

  "Future Implicit Conversions" - {
    "twitter Future should be convert to scala Future" in {
      val tfa = twitter.Future.value(10)
      val sfa = tfa.toScalaFuture
      Await.result(sfa, 1 second) shouldBe twitter.Await.result(tfa)

      val tfb = twitter.Future.exception(new IllegalStateException("Error"))
      val sfb = tfb.toScalaFuture
      intercept[IllegalStateException] {
        Await.result(sfb, 1 second)
      }
    }

    "scala Future should be convert to twitter Future" in {
      val sfa = Future.successful(10)
      val tfa = sfa.toTwitterFuture
      twitter.Await.result(tfa) shouldBe Await.result(sfa, 1 second)

      val sfb = Future.failed(new IllegalStateException("Error"))
      val tfb = sfb.toTwitterFuture
      intercept[IllegalStateException] {
        twitter.Await.result(tfb)
      }

    }

    "twitter Try should be convert to scala Try" in {
      val tta = twitter.Return(10)
      val sta : Try[Int] = tta
      sta shouldBe Success(10)

      val ex = new IllegalStateException("Error")
      val ttb = twitter.Throw(ex)
      val stb : Try[Int] = ttb
      stb shouldBe Failure(ex)
    }

    "scala Try should be to convert twitter Try" in {
      val sta = Success(10)
      val tta : twitter.Try[Int] = sta
      tta shouldBe twitter.Return(10)

      val ex = new IllegalStateException("Error")
      val stb = Failure(ex)
      val ttb : twitter.Try[Int] = stb
      ttb shouldBe twitter.Throw(ex)
    }

  }
}
