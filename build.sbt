name := "scizzors"
version := "NA"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalaj"  %% "scalaj-http"    % "2.3.0",
  "com.github.scopt" %% "scopt"     % "3.4.0",
  "com.lihaoyi" %% "upickle"        % "0.4.1",
  "com.lihaoyi" %% "ammonite-ops"   % "0.6.2",
  "com.lihaoyi" %% "ammonite-repl"  % "0.6.2" cross CrossVersion.full
)

initialCommands in console := """
  import scizzors._
  object Preamble0 extends Addons; import Preamble0._
"""
