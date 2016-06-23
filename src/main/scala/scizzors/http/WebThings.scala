package scizzors

import ammonite.repl.Ref
import scalaj.http._

/** 
  */
trait WebThings {

  /** Connection timeout under scizzors environment. */
  val connTimeout: Ref[Int] = Ref(5000)
  /** Reading timeout under scizzors environment. */
  val readTimeout: Ref[Int] = Ref(5000)

  /** Proxy to scalaj.HttpRequest builder overrides default timeout limits. */
  private def timeoutDefaulted(url: String): HttpRequest =
    Http(url).timeout(connTimeout(), readTimeout())

  def apply(url: String): HttpRequest = timeoutDefaulted(url)

  def !(arg: String) = apply(arg)
}