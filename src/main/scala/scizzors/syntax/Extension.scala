package scizzors.syntax

import collection.GenIterable
import collection.IterableLike
import collection.generic.CanBuildFrom

class OptionExt[+T](o: Option[T]) {

  def |[B](f: T => B): Option[B] = o map f

  def ||[B](f: T => Option[B]): Option[B] = o flatMap f

  def |!(f: T => Unit): Unit = o foreach f

  def |?(p: T => Boolean): Option[T] = o filter p

  def ??[T1 >: T](default: => T1): T1 = o getOrElse default

  def ?[T1 >: T](alternative: => Option[T1]): Option[T1] = o orElse alternative
}

class Zipping[+A, Repr](itl: IterableLike[A, Repr]) {

  def |+[A1 >: A, B, That](that: GenIterable[B])(
      implicit bf: CanBuildFrom[Repr, (A1, B), That]): That = {
    itl.zip(that)(bf)
  }

  def |++[A1 >: A, B, That](that: GenIterable[B], thisElem: A1, thatElem: B)(
      implicit bf: CanBuildFrom[Repr, (A1, B), That]): That = {
    itl.zipAll(that, thisElem, thatElem)(bf)
  }

  def |+#[A1 >: A, That](implicit bf: CanBuildFrom[Repr, (A1, Int), That]): That = {
    itl.zipWithIndex(bf)
  }
}

class QueryableSeq[+A](seq: collection.Seq[A]) {

  def applyOption(i: Int): Option[A] = try Some(seq(i)) catch {
    case e: IndexOutOfBoundsException => None
  }

  def /?(i: Int) = applyOption(i)

  def pick(numbers: Int*) = for (n <- numbers) yield seq(n)
}

class QueryableMap[A, B](map: collection.Map[A, B]) {

  def /?(key: A) = map get key

  def ??=(q: (A, B)) = map getOrElse(q._1, q._2)
}


