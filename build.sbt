import Dependencies.Compile._

lazy val commonSettings = Seq(
  organizationName := "Xebia Functional",
  startYear := Some(2023),
  licenses += ("Apache-2.0", new URL(
    "https://www.apache.org/licenses/LICENSE-2.0.txt"
  )),
  crossScalaVersions := Seq(
    "3.2.2",
    "3.2.1",
    "3.2.0",
    "3.1.3",
    "3.1.2",
    "3.1.1",
    "3.1.0",
    "3.0.2",
    "3.0.1",
    "3.0.0"
  )
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "munit-compiler-toolkit",
    version := "0.1.0-SNAPSHOT"
  )
  .aggregate(`munit-compiler-toolkit-fixtures`)

lazy val `munit-compiler-toolkit-fixtures` = project
  .in(file("./munit-compiler-toolkit-fixtures"))
  .settings(commonSettings)
  .settings(
    libraryDependencies += munit,
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
  )
  .enablePlugins(AutomateHeaderPlugin)
