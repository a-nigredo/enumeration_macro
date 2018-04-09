package dev.nigredo

/**
  * Dual to cats.Show
  */
trait Read[A] {
  def read(str: String): Option[A]
}
