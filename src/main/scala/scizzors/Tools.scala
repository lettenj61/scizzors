package scizzors

import java.nio.file.Files
import ammonite.ops._

/** Some built-in tools to override Ammonite ops' ones.
  *
  * These are implemented to let Ammonite's cool functionality fit in
  * Windows' poor command prompt.
  */
trait Tools { self =>

  /** Equivalent to `ammonite.ops.LsSeq`
    */
  case class Trail(base: Path, trails: RelPath*) extends Seq[Path] {
    def length = trails.length
    def apply(index: Int) = base / trails.apply(index)
    def iterator = trails.iterator.map(base./)

    override def toString = trails.map(tr => tr.last).mkString(", ")
  }

  case class Recursive(skip: Path => Boolean = _ => false,
                       previousOrder: Boolean = false)
  extends StreamableOp1[Path, Path, Trail] with ImplicitOp[Trail] { rec =>
    def materialize(src: Path, i: Iterator[Path]) = self.ls.materialize(src, i)

    object iter extends (Path => Iterator[Path]) {

      def apply(arg: Path) = {
        def items = ls.iter(arg)
        for {
          item <- items
          if !skip(item)
          sub <- {
            if (!stat(item).isDir) Iterator(item)
            else {
              val children = apply(item)
              if (previousOrder) Iterator(item) ++ children
              else children ++ Iterator(item)
            }
          }
        } yield sub

      }

    }
  }

  object ls extends StreamableOp1[Path, Path, Trail] with ImplicitOp[Trail] {
    def materialize(src: Path, rels: Iterator[Path]) =
      Trail(src, rels.map(_ relativeTo src).toVector.sorted :_*)

    object iter extends (Path => Iterator[Path]) {
      def apply(arg: Path) = {
        import scala.collection.JavaConverters._
        val directories = Files.newDirectoryStream(arg.toNIO)
        new SelfClosingIterator(
          directories.iterator.asScala.map(x => Path(x)),
          () => directories.close()
        )
      }
    }
  }

  object stat extends (Path => Attrs) {
    def apply(path: Path) = Attrs default path

    def !(path: Path) = apply(path)
  }

  /** This `sgrep` is for "Silent Grep Function", which replaces original
    * grep tool with no pretty printing output.
    */
  object sgrep {

  }
}
