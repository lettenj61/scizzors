package scizzors

object Utils {

  def dquot(cs: CharSequence): String = enclose("\"", cs)

  def tdquot(cs: CharSequence): String = enclose(("\"" * 3), cs)

  def enclose(closer: String, cs: CharSequence): String = {
    new StringBuilder(closer).append(cs).append(closer).toString
  }
}