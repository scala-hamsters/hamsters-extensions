package io.github.hamsters

import scala.annotation.implicitNotFound

object HListExtensions {
  implicit class HConsWithSelector[T, U <:HList](hCons: HCons[T, U]) {
    def select[O](implicit selector: Selector[HCons[T, U], O]): O = selector(hCons)
  }
}

@implicitNotFound("Implicit not found: io.github.hamsters[${L}, ${O}]. You requested an element of type ${O}, but there is none in the HList ${L}.")
trait Selector[L <: HList, O] {
  def apply(l: L): O
}

object Selector {

  def apply[L <: HList, O](implicit selector: Selector[L, O]) = selector

  implicit def select[H, T <: HList] = new Selector[HCons[H, T], H] {
    override def apply(l: HCons[H, T]): H = l.head
  }

  implicit def recurse[H, T <: HList, O](implicit st : Selector[T, O]) = new Selector[HCons[H, T], O] {
    override def apply(l: HCons[H, T]): O = st.apply(l.tail)
  }


}
