package scizzors

import ammonite.repl.frontend._

object AnsiWindowsFrontEnd extends FrontEnd.JLineTerm(() => new jline.AnsiWindowsTerminal)
