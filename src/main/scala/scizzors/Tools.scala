package scizzors

import java.nio.file.Files
import ammonite.ops._

object stat extends (Path => Attrs) {
  def apply(path: Path) = Attrs default path

  def !(path: Path) = apply(path)
}

/** This `sgrep` is for "Silent Grep Function", which replaces Ammonite's original
  * grep tool with no pretty printing output.
  */
object sgrep { self =>
  import util.matching.Regex

  def apply[R](regex: Regex)(subject: R): Boolean = regex.findAllIn(subject.toString).nonEmpty
  def !(regex: Regex) = apply(regex) _
  def !(regex: String) = apply(regex.r) _

  def ?(regex: Regex)(subject: Any): Seq[Regex.Match] = regex.findAllMatchIn(subject.toString).toList
  def ?(regex: String)(subject: Any): Seq[Regex.Match] = self.?(regex.r)(subject)

  object not {
    def apply[R](regex: Regex)(subject: R): Boolean = regex.findAllIn(subject.toString).isEmpty
    def !(regex: Regex) = not.apply(regex) _
    def !(regex: String) = not.apply(regex.r) _
  }
}

/** Module to handle date & time operations from Ammonite.
  */
object datetime {
  import java.time._

  def now = LocalDateTime.now

  def today = {
    val T = now
    LocalDateTime.of(T.getYear, T.getMonthValue, T.getDayOfMonth, 0, 0)
  }

  def yesterday = today minusDays 1

  def tomorrow = today plusDays 1

  def zone = ZoneId.systemDefault
}
