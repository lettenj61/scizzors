name := "scizzors"
version := "NA"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config"         % "1.3.0",
  "org.scalaj"  %% "scalaj-http"    % "2.3.0",
  "com.github.scopt" %% "scopt"     % "3.4.0",
  "com.lihaoyi" %% "upickle"        % "0.4.1",
  "com.lihaoyi" %% "ammonite-ops"   % "0.6.2",
  "com.lihaoyi" %% "ammonite-repl"  % "0.6.2" cross CrossVersion.full
)

initialCommands in (Test, console) := """
  import ammonite.{ ops, repl }; import ops._
  repl.Main.main(
    Seq(
      "-f",
      cwd/'src/'main/'resources/"preamble.scala" toString
    ).toArray
  )
"""
