package scizzors

import ammonite.ops.{ Command, Path, Shellout, Shellable }

/** Module to pipe Ammonite `%` / `%%` syntax command builders
  * with Windows' cmd.exe operation.
  */
trait WindowsHelper {

  class WindowsCommand[T](override val cmd: Vector[String],
                          override val envArgs: Map[String, String],
                          override val execute: (Path, Command[_]) => T)
    extends Command[T](cmd, envArgs, execute) {

    protected def executable = Vector("cmd", "/c")
    override def opArg(op: String) = executable ++ super.opArg(op)

    override def selectDynamic(name: String)(implicit wd: Path) =
      execute(wd, extend((executable :+ name), Map()))

    override def applyDynamic(op: String)(args: Shellable*)(implicit wd: Path) = {
      execute(wd, this.extend(opArg(op) ++ args.flatMap(_.s), Map()))
    }

    override def applyDynamicNamed(op: String)(args: (String, Shellable)*)(implicit wd: Path): T = {
      val(namedArgs, posArgs) = args.map { case (k, v) => (k, v.s) }.partition(_._1 != "")
      execute(
        wd,
        this.extend(
          opArg(op) ++ posArgs.flatMap(_._2),
          namedArgs.map { case (k, v) => (k, v.head) }
        )
      )
    }
  }
}