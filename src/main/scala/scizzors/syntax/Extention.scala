package scizzors.syntax


class FilterMapOptional[+T](o: Option[T]) {

  def |[B](f: T => B): Option[B] = o map f

  def ||[B](f: T => Option[B]): Option[B] = o flatMap f

  def |!(f: T => Unit): Unit = o foreach f

  def |?(p: T => Boolean): Option[T] = o filter p
}