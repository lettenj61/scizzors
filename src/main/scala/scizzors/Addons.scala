package scizzors

import scala.language.implicitConversions

import ammonite.repl.Ref
import ammonite.ops
import ops.Path

import upickle.default

/**
  */
trait Addons {

  /** Grants reference to the path where the application focus on.
    */
  trait PathRef extends ops.ImplicitOp[Path] {
    private[scizzors] val focused: Ref[Path] = Ref(ops.Path.home)
  }

  /** Switch reference of `current` directory.
    */
  object cd extends PathRef {
    def apply(arg: Path): Path = {
      focused() = arg
      focused()
    }
  }

  /** A default implicit path provision.
    */
  implicit def wd: Path = cd.focused()

  implicit def ChainableOption[A](option: Option[A]) = new syntax.FilterMapOptional(option)

  /** Functionality to handle scalaj.Http things.
    */
  object http extends WebThings

  /** Pickling dsl.
    */
  object pickle {
    def apply[T : default.Writer](expr: T, indent: Int = 2) = default.write(expr, indent)

    object save {
      def apply[T : default.Writer](arg: Path, expr: T, indent: Int = 2) = {
        ops.write(arg, pickle.apply(expr, indent))
      }

      def over[T : default.Writer](arg: Path, expr: T, indent: Int = 2) = {
        ops.write.over(arg, pickle.apply(expr, indent))
      }
    }
  }

  /** And the other.
    */
  object unpickle {
    def apply[T : default.Reader](expr: String) = default.read(expr)

    object file {
      def apply[T : default.Reader](arg: Path) = default.read(ops.read(arg))

      def asJs(arg: Path) = upickle.json.read(ops.read(arg)).asInstanceOf[upickle.Js.Obj]
    }
  }
}