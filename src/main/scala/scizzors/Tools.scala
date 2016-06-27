package scizzors

import ammonite.ops._

/** Some built-in tools to override Ammonite ops' ones.
  *
  * These are implemented to let Ammonite's cool functionality fit in
  * Windows' poor command prompt.
  */
trait Tools {

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
