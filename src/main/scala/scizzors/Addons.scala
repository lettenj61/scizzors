package scizzors

import scala.language.implicitConversions

import ammonite.repl.Ref
import ammonite.ops
import ops.Path

import upickle.default

/** Additional functions to regular Ammonite operations.
  */
trait Addons {

  /** Grants reference to the path where the application focus on.
    */
  trait PathRef extends ops.ImplicitOp[Unit] {
    private[scizzors] val focus: Ref[Path] = Ref(ops.cwd)

    override def toString = focus().toString
  }

  /** Switch reference of `current` directory.
    */
  object cd extends PathRef {
    def apply(arg: Path): Unit = {
      if (ops.exists! arg) {
        if (arg.isDir) {
          focus() = arg
          println(focus())
        } else {
          println(s"$arg is not a directory.")
        }
      } else println(s"$arg does not exist.")
    }

    def ~ = apply(ops.Path.home)
  }

  /** A default implicit path provider.
    */
  implicit def wd: Path = cd.focus()

  /** Experimental!
    * Run an executable located in specified path with sub process.
    */
  object exec {

    def apply(executable: Path) = ops.%(executable.toString)
    def !(executable: Path) = apply(executable)
  }

  /** Rough helper to some platform-oriented operations.
    */
  object windows extends WindowsHelper {

    lazy val os = sys.props("os.name")
    private[this] lazy val reusableCommandI = new WindowsCommand(
      Vector.empty, Map.empty, ops.Shellout.executeInteractive)

    private[this] lazy val reusableCommandS = new WindowsCommand(
      Vector.empty, Map.empty, ops.Shellout.executeStream)

    /** Spawns subprocesses which defined as subcommand of Windows' `cmd.exe`,
     *  by passing arguments implicitly prefixed with "cmd /c".
     */
    def %# = {
      if (os.toLowerCase contains "windows") reusableCommandI
      else unsupportedPlatform("%# syntax")
    }

    /** Similar to `%#` operator, but receives result of sub process as `CommandResult`s.
      */
    def %%# = {
      if (os.toLowerCase contains "windows") reusableCommandS
      else unsupportedPlatform("%%# syntax")
    }

    private def unsupportedPlatform(operation: String) = {
      throw new UnsupportedOperationException(s"$operation is not supported in $os")
    }
  }

  /** Pickling functionality to avoid naming conflict between uPickle and Ammonite-Ops
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

  /** Unpickling functionality supports reading json files from disks.
    */
  object unpickle {
    import upickle.json
    def apply[T : default.Reader](expr: String) = default.read(expr)

    def asJs(expr: String) = json.read(expr).asInstanceOf[upickle.Js.Obj]

    object file {
      def apply[T : default.Reader](arg: Path) = default.read(ops.read(arg))

      def asJs(arg: Path) = unpickle.asJs(ops.read(arg))
    }
  }
}
