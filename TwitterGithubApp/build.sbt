name := "TwitterGithubApp"

version := "0.1.0"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.2",
  "com.typesafe" % "config" % "1.3.2",
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
