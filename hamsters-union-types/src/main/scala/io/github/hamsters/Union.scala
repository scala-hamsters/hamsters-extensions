package io.github.hamsters

import scala.reflect._
import scala.collection.immutable.Seq
import scala.meta.Term.Name
import scala.meta.{Term, _}

/*
class UnionMacro extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    def argByXY(idx: Int, i: Int): Term = if (idx == i) {
      q"""Some(t)"""
    }
    else {
      q"""None"""
    }

    defn match {
      case q"class $className[..$classTypes] { ..$body }" =>
        val types = classTypes.map(t => Type.Name(t.name.value))
        val typesName: Seq[Name] = classTypes.map(t => Term.Name(t.name.value))
        val result =
          q"""
        class $className[..$classTypes] {
        ..$body
         ..${

            typesName.zipWithIndex.map { case (currentTypeName, y) =>
              val unionXType2UnionXMethodName = Term.Name("toUnion" + y)
              val currentType = Type.Name(currentTypeName.value)
              val unionXType = Term.Name("Union" + classTypes.size)
              val constructorArgs : Seq[Term.Arg] = for (x <- typesName.indices) yield q"""${argByXY(x, y)}"""
              q"implicit def $unionXType2UnionXMethodName(t : $currentType) = $unionXType[..$types](..$constructorArgs)"
            }
          }
        }
          """
        //println(result.syntax) display code result
        result
      case _ =>
        println(defn.structure)
        abort("@UnionMacroMeta must annotate a class")
    }
  }
}

*/

private object Union {
  implicit class NoEraseInstanceOf(that: Any) {
    def noEraseInstanceOf[T: ClassTag]= that match {
      case _: T => true
      case _ => false
    }
  }
}

trait Union { this: Product =>

  @scala.annotation.tailrec
  private def findTypeOnProductIterator[T : ClassTag](it: Iterator[Any]): Option[T] = {
    import Union._
    if(!it.hasNext) None
    else {
      val current = it.next()
      current match {
        case Some(u) if u.noEraseInstanceOf[T] => Some(u).asInstanceOf[Some[T]]
        case _ => findTypeOnProductIterator(it)
      }
    }
  }

  /**
   * Get an Option of result for a given type
   * @tparam T
   * @return Option of result for T
   */
  def get[T : ClassTag] : Option[T] = findTypeOnProductIterator(this.productIterator)

  /**
   * Get result for a given type, or a default value if type is not found
   * @param default
   * @tparam T
   * @return result for T or default
   */
  def getOrElse[T : ClassTag](default: T): T = get[T].getOrElse(default)

}

case class Union2[T1, T2](v1: Option[T1], v2: Option[T2]) extends Union

case class Union3[T1, T2, T3](v1: Option[T1], v2: Option[T2], v3: Option[T3]) extends Union

case class Union4[T1, T2, T3, T4](v1: Option[T1], v2: Option[T2], v3: Option[T3], v4: Option[T4]) extends Union

case class Union5[T1, T2, T3, T4, T5](v1: Option[T1], v2: Option[T2], v3: Option[T3], v4: Option[T4], v5: Option[T5]) extends Union

case class Union6[T1, T2, T3, T4, T5, T6](v1: Option[T1], v2: Option[T2], v3: Option[T3], v4: Option[T4], v5: Option[T5], v6 : Option[T6]) extends Union

case class Union7[T1, T2, T3, T4, T5, T6, T7](v1: Option[T1], v2: Option[T2], v3: Option[T3], v4: Option[T4], v5: Option[T5], v6 : Option[T6], v7 : Option[T7]) extends Union

class Union2Type[T1, T2] {
  implicit def toUnion21(t: T1)(implicit m1: Manifest[T1], m2: Manifest[T2]) = new Union2[T1, T2](Some(t), None)

  implicit def toUnion22(t: T2)(implicit m1: Manifest[T1], m2: Manifest[T2]) = new Union2[T1, T2](None, Some(t))
}

class Union3Type[T1, T2, T3] {
  implicit def toUnion31(t: T1) = new Union3[T1, T2, T3](Some(t), None, None)

  implicit def toUnion32(t: T2) = new Union3[T1, T2, T3](None, Some(t), None)

  implicit def toUnion33(t: T3) = new Union3[T1, T2, T3](None, None, Some(t))
}

class Union4Type[T1, T2, T3, T4] {
  implicit def toUnion41(t: T1) = new Union4[T1, T2, T3, T4](Some(t), None, None, None)

  implicit def toUnion42(t: T2) = new Union4[T1, T2, T3, T4](None, Some(t), None, None)

  implicit def toUnion43(t: T3) = new Union4[T1, T2, T3, T4](None, None, Some(t), None)

  implicit def toUnion44(t: T4) = new Union4[T1, T2, T3, T4](None, None, None, Some(t))

}

class Union5Type[T1, T2, T3, T4, T5] {
  implicit def toUnion51(t: T1) = new Union5[T1, T2, T3, T4, T5](Some(t), None, None, None, None)

  implicit def toUnion52(t: T2) = new Union5[T1, T2, T3, T4, T5](None, Some(t), None, None, None)

  implicit def toUnion53(t: T3) = new Union5[T1, T2, T3, T4, T5](None, None, Some(t), None, None)

  implicit def toUnion54(t: T4) = new Union5[T1, T2, T3, T4, T5](None, None, None, Some(t), None)

  implicit def toUnion55(t: T5) = new Union5[T1, T2, T3, T4, T5](None, None, None, None, Some(t))
}

/*
class UnionMacro extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    def argByXY(idx: Int, i: Int): Term = if (idx == i) {
      q"""Some(t)"""
    }
    else {
      q"""None"""
    }

    defn match {
      case q"class $className[..$classTypes] { ..$body }" =>
        val types = classTypes.map(t => Type.Name(t.name.value))
        val typesName: Seq[Name] = classTypes.map(t => Term.Name(t.name.value))
        val result =
          q"""
        class $className[..$classTypes] {
        ..$body
         ..${

            typesName.zipWithIndex.map { case (currentTypeName, y) =>
              val unionXType2UnionXMethodName = Term.Name("toUnion" + y)
              val currentType = Type.Name(currentTypeName.value)
              val unionXType = Term.Name("Union" + classTypes.size)
              val constructorArgs : Seq[Term.Arg] = for (x <- typesName.indices) yield q"""${argByXY(x, y)}"""
              q"implicit def $unionXType2UnionXMethodName(t : $currentType) = $unionXType[..$types](..$constructorArgs)"
            }
          }
        }
          """
        //println(result.syntax) display code result
        result
      case _ =>
        println(defn.structure)
        abort("@UnionMacroMeta must annotate a class")
    }
  }
}

*/

//@UnionMacro
//class Union6Type[T1, T2, T3, T4, T5, T6]

//@UnionMacro
//class Union7Type[T1, T2, T3, T4, T5, T6, T7]

