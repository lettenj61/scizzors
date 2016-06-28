package scizzors

import java.nio.file.Files
import ammonite.ops._

object stat extends (Path => Attrs) {
  def apply(path: Path) = Attrs default path

  def !(path: Path) = apply(path)
}

/** This `sgrep` is for "Silent Grep Function", which replaces original
  * grep tool with no pretty printing output.
  */
object sgrep {

}

/** Module to handle date & time operations from Ammonite.
  */
object datetime {
  import java.time._

  def now = LocalDateTime.now

  def yesterday = now minusDays 1

  def tomorrow = now plusDays 1

  def zone = ZoneId.systemDefault
}
