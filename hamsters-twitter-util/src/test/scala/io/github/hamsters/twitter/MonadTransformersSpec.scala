package io.github.hamsters.twitter

import java.util.concurrent.TimeUnit

import com.twitter.util._
import org.scalatest._
import org.scalactic._

import scala.concurrent.ExecutionContext.Implicits.global

class MonadTransformersSpec extends FreeSpec with Matchers {

  "twitter.FutureOption" - {
    "for comprehension composition" - {
      "should handle com.twitter.util.Future[Option[_]] type" in {

        def foa: Future[Option[String]] = Future(Some("a"))
        def fob(a: String): Future[Option[String]] = Future(Some(a + "b"))

        val composedAB: Future[Option[String]] = (for {
          a <- FutureOption(foa)
          ab <- FutureOption(fob(a))
        } yield ab).future

        Await.result(composedAB, Duration(1, TimeUnit.SECONDS)) shouldBe Some("ab")

        val composedABWithNone: Future[Option[String]] = (for {
          a <- FutureOption(Future.value(None))
          ab <- FutureOption(fob(a))
        } yield ab).future

        Await.result(composedABWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe None

        val composedABWithFailure: Future[Option[String]] = (for {
          a <- FutureOption(Future.exception(new Exception("d'oh!")))
          ab <- FutureOption(fob(a))
        } yield ab).future

        an[Exception] should be thrownBy Await.result(composedABWithFailure, Duration(1, TimeUnit.SECONDS))
      }
    }

    "filter" - {
      def foa: Future[Option[(String, Int)]] = Future(Some(("a", 42)))
      def fob: Future[Option[(String, Int)]] = Future(None)

      "should be filtered with patter matching" in {
        val filtered = FutureOption(foa).filter { case ((_, i)) => i > 5 }.map(_._1).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Some("a")

        val filtered2 = FutureOption(foa).filter { case ((_, i)) => i > 50 }.map(_._1).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe None

        val filteredWithNone = FutureOption(fob).filter { case ((_, i)) => i > 5 }.map(_._1).future

        Await.result(filteredWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe None
      }

      "shoud be filtered with pattern matching in for comprehension" in {
        val filtered = (for {
          (a, i) <- FutureOption(foa) if i > 5
        } yield a).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Some(("a"))

        val filtered2 = (for {
          (a, i) <- FutureOption(foa) if i > 50
        } yield a).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe None

        val filteredWithNone = (for {
          (a, i) <- FutureOption(fob) if i > 5
        } yield a).future

        Await.result(filteredWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe None
      }
    }
  }

  "twitter.FutureEither" - {
    "for comprehension composition" - {
      "shoud handle com.twitter.util.Future[Either[_,_]] type" in {
        def fea: Future[Either[String, Int]] = Future(Right(1))
        def feb(a: Int): Future[Either[String, Int]] = Future(Right(a + 2))

        val composedAB: Future[Either[String, Int]] = (for {
           a <- FutureEither(fea)
          ab <- FutureEither(feb(a))
        } yield ab).future

        Await.result(composedAB, Duration(1, TimeUnit.SECONDS)) shouldBe Right(3)

        val composedABWithNone: Future[Either[String, Int]] = (for {
           a <- FutureEither(Future.value(Left("d'oh!")))
          ab <- FutureEither(feb(a))
        } yield ab).future

        Await.result(composedABWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe Left("d'oh!")

        val composedABWithFailure: Future[Either[String, Int]] = (for {
           a <- FutureEither(Future.exception(new Exception("d'oh!")))
          ab <- FutureEither(feb(a))
        } yield ab).future

        an[Exception] should be thrownBy Await.result(composedABWithFailure, Duration(1, TimeUnit.SECONDS))
      }
    }

    "filter" - {
      def fea: Future[Either[String, (String, Int)]] = Future(Right(("a", 42)))
      def feb: Future[Either[String, (String, Int)]] = Future(Left("d'oh!"))
      "should be filtered with pattern matching" in {
        val filtered = FutureEither(fea).filter { case ((_, i)) => i > 5 }.map(_._1).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Right("a")

        val filtered2 = FutureEither(fea).filter { case ((_, i)) => i > 50 }.map(_._2).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe Left("No value matching predicate")

        val filteredWithNone = FutureEither(feb).filter { case ((_, i)) => i > 5 }.map(_._2).future

        Await.result(filteredWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe Left("No value matching predicate")
      }

      "should be filtered with pattern matching in for comprehension" in {
        val filtered = (for {
          (a, i) <- FutureEither(fea) if i > 5
        } yield a).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Right("a")

        val filtered2 = (for {
          (a, i) <- FutureEither(fea) if i > 50
        } yield a).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe Left("No value matching predicate")

        val filteredWithNone = (for {
          (a, i) <- FutureEither(feb) if i > 5
        } yield a).future

        Await.result(filteredWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe Left("No value matching predicate")
      }
    }
  }

  "twitter.FutureOr" - {
    "for comprehension composition" - {
      "should handle com.twitter.util.Future[Or[_,_]] type" in {
        val fora: Future[String Or Int] = Future(Good("This is"))
        val forb: Future[String Or Int] = Future(Good("good!"))

        val composedAB: Future[String Or Int] = (for {
             a <- FutureOr(fora)
            ab <- FutureOr(forb)
        } yield s"$a $ab").future

        Await.result(composedAB, Duration(1, TimeUnit.SECONDS)) shouldBe Good("This is good!")

        val composedABWithNone: Future[String Or Int] = (for {
             a <- FutureOr(Future(Bad(0)))
            ab <- FutureOr(forb)
        } yield s"$a $ab").future

        Await.result(composedABWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe Bad(0)

        val composedABWithFailure: Future[String Or Int] = (for {
           a <- FutureOr(Future.exception(new Exception("d'oh!")))
          ab <- FutureOr(forb)
        } yield s"$a $ab").future

        an[Exception] should be thrownBy Await.result(composedABWithFailure, Duration(1, TimeUnit.SECONDS))
      }
    }

    "filter" - {
      val fora: Future[(String, Int) Or Int] = Future(Good("This is a good number!" -> 10))
      val forb: Future[(String, Int) Or Int] = Future(Bad(5))

      "should be filtered with pattern matching" in {
        val filtered = FutureOr(fora).filter { case (_, i) => i > 5 }.map(_._1).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Good("This is a good number!")

        val filtered2 = FutureOr(fora).filter { case (_, i) => i > 20 }.map(_._2).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe Bad(Error("No value matching predicate"))

        val filteredWithNone = FutureOr(forb).filter { case (_, i) => i > 5 }.map(_._2).future

        Await.result(filteredWithNone, Duration(1, TimeUnit.SECONDS)) shouldBe Bad(Error("No value matching predicate"))
      }

      "should be filtered with pattern matching in for comprehension" in {
        val filtered = (for {
          (a, i) <- FutureOr(fora) if i > 5
        } yield a).future

        Await.result(filtered, Duration(1, TimeUnit.SECONDS)) shouldBe Good("This is a good number!")

        val filtered2 = (for {
          (a, i) <- FutureOr(fora) if i > 50
        } yield a).future

        Await.result(filtered2, Duration(1, TimeUnit.SECONDS)) shouldBe Bad(Error("No value matching predicate"))
      } 
    }
  }
}
