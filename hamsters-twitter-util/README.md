# Hamsters Extentions - Hamsters Twitter Util
A micro Scala utility library [Hamsters](https://github.com/scala-hamsters/hamsters) with [Twitter util](https://github.com/twitter/util). Compatible with functional programming beginners :)

![hamster picture](http://loicdescotte.github.io/images/hamster.jpg)
![twitter picture](https://avatars1.githubusercontent.com/u/50278?v=3&s=300)

##  Simple monad transformers with [Twitter Future](https://github.com/twitter/util/blob/master/util-core/src/main/scala/com/twitter/util/Future.scala)

Example : combine twitter Future and Option types then make it work in a for comprehension.
Original idea comes from [Hamsters FutureOption](https://github.com/scala-hamsters/hamsters#simple-monad-transformers).
More information on why it's useful [here](http://loicdescotte.github.io/posts/scala-compose-option-future/).

### Twitter Future

Simple Compositions Two Future Options.
```scala
import com.twitter.util.Future
import io.github.hamsters.twitter.FutureOption

def foa: Future[Option[String]] = Future(Some("a"))
def fob(a: String): Future[Option[String]] = Future(Some(a+"b"))

val composedAB: Future[Option[String]] = (for {
  a <- FutureOption(foa)
  ab <- FutureOption(fob(a))
} yield ab).future
```

Compositions with filter Two Future Options.
```scala
import com.twitter.util.Future
import io.github.hamsters.twitter.FutureOption

def foa: Future[Option[Int]] = Future(Some(10))
def fob(a: Int): Future[Option[Int]] = Future(Some(a + 20))

val composedAB: Future[Option[Int]] = (for {
  a <- FutureOption(foa) if a > 5
  ab <- FutureOption(fob(a))
} yield ab).future
```

### Future conversions
Conversions between [scala Future](http://docs.scala-lang.org/overviews/core/futures.html) and [twitter Future](https://github.com/twitter/util#futures).
It is useful when you write code two different futures(ie. [twitter scala open source project](https://engineering.twitter.com/opensource/projects?tags%5B%5D=2) and other open source)

From Scala to Twitter
```scala
import scala.concurrent.Future
import com.twitter.util.{Future => TwitterFuture}
import io.github.hamsters.twitter.Implicits._

val futureA = Future.successful("A")
val futureB : TwitterFuture[String] = futureA.toTwitterFuture
```

From Twitter to Scala
```scala
import com.twitter.util.Future
import scala.concurrent.{Future => ScalaFuture}
import io.github.hamsters.twitter.Implicits._

val futureA = Future.value("A")
val futureB : ScalaFuture[String] = futureA.toScalaFuture
```
