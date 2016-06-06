package io.github.hamsters.twitter

import com.twitter.util.Future
import org.scalactic._

import scala.concurrent.ExecutionContext

case class FutureOption[+A](future: Future[Option[A]]) extends AnyVal {
  def flatMap[B](f: A => FutureOption[B])(implicit ec: ExecutionContext): FutureOption[B] =
    FutureOption(future.flatMap {
      case Some(a) => f(a).future
      case None => Future.value(None)
    })

  def map[B](f: A => B)(implicit ec: ExecutionContext): FutureOption[B] = FutureOption(future.map(_.map(f)))

  def filter(p: A ⇒ Boolean)(implicit ec: ExecutionContext): FutureOption[A] = withFilter(p)(ec)

  def withFilter(p: A ⇒ Boolean)(implicit ec: ExecutionContext): FutureOption[A] =
    FutureOption(future.map {
      case Some(a) => if (p(a)) Some(a) else None
      case _ => None
    })
}

case class FutureEither[L, +R](future: Future[Either[L, R]]) extends AnyVal {
  def flatMap[R2](f: R => FutureEither[L, R2])(implicit ec: ExecutionContext): FutureEither[L, R2] =
    FutureEither(future.flatMap {
      case Right(r) => f(r).future
      case Left(l) => Future.value(Left(l))
    })

  def map[R2](f: R => R2)(implicit ec: ExecutionContext): FutureEither[L, R2] = FutureEither(future.map(_.right map f))

  def filter(p: (R) ⇒ Boolean)(implicit ec: ExecutionContext): FutureEither[String, R] = withFilter(p)(ec)

  def withFilter(p: (R) ⇒ Boolean)(implicit ec: ExecutionContext): FutureEither[String, R] =
    FutureEither(future.map {
      case Right(r) => if(p(r)) Right(r) else Left("No value matching predicate")
      case _ => Left("No value matching predicate")
    })

  def filterWithDefault(p: (R) ⇒ Boolean, default: L)(implicit ec: ExecutionContext): FutureEither[L, R] =
    FutureEither(future.map {
      case Right(r) => if(p(r)) Right(r) else Left(default)
      case _ => Left(default)
    })
}

case class FutureOr[+G, B](future: Future[G Or B]) extends AnyVal {
  def flatMap[G2](f: G => FutureOr[G2, B])(implicit ec: ExecutionContext): FutureOr[G2, B] = 
    FutureOr(future.flatMap {
      case Good(g) => f(g).future
      case Bad(b)  => Future(Bad(b))
    })

  def map[G2](f: G => G2)(implicit ec: ExecutionContext): FutureOr[G2, B] = 
    FutureOr(future.map {
      case Good(g) => Good(f(g))
      case Bad(b)  => Bad(b)
    })

  def filter(p: (G) => Boolean)(implicit ec: ExecutionContext): FutureOr[G, Error] = withFilter(p)(ec)

  def withFilter(p: (G) => Boolean)(implicit ec: ExecutionContext): FutureOr[G, Error] = {
    FutureOr(future.map {
      case Good(g) => if (p(g)) Good(g) else Bad(Error("No value matching predicate"))
      case Bad(b)  => Bad(Error("No value matching predicate"))
    })
  }
}

case class Error(value: String) extends AnyVal


