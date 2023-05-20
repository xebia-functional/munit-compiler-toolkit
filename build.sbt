import Dependencies.Compile._

ThisBuild / scalaVersion := "3.2.1"

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

lazy val MunitFramework = new TestFramework("munit.Framework")

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "munit-compiler-toolkit",
    version := "0.1.0-SNAPSHOT"
  )
  .aggregate(`munit-compiler-toolkit-testkit`, testCompilerPlugin)

lazy val testCompilerPlugin = project
  .in(file("./munit-compiler-toolkit-testPlugin"))
  .settings(commonSettings)
  .settings(
    exportJars := true,
    publish / skip := true,
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value,
    Test / testOptions += Tests.Argument(
      MunitFramework,
      "-Dscala-compiler-plugins:${(testCompilerPlugin / Compile / packageBin).value}"
    ),
    Test / testOptions += Tests.Argument(
      MunitFramework,
      s"-Dscala-compiler-classpath=:${(Compile / dependencyClasspath).value.files
          .map(_.toPath().toAbsolutePath().toString())
          .mkString(":")}"
    ),
    Test / testOptions += Tests.Argument(
      MunitFramework,
      s"""-Dcompiler-scalacOptions="${scalacOptions.value.mkString(" ")}""""
    )
  )
  .dependsOn(`munit-compiler-toolkit-testkit`)

lazy val `munit-compiler-toolkit-testkit` = project
  .in(file("./munit-compiler-toolkit-testkit"))
  .settings(commonSettings)
  .settings(
    libraryDependencies += munit,
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
  )
  .enablePlugins(AutomateHeaderPlugin)
