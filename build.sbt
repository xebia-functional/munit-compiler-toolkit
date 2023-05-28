import Dependencies.Compile._

ThisBuild / scalaVersion := "3.2.2"
ThisBuild / organization := "com.47deg"
ThisBuild / versionScheme := Some("early-semver")

addCommandAlias(
  "ci-test",
  "scalafmtCheckAll; scalafmtSbtCheck; github; documentation / mdoc; root / test"
)
addCommandAlias("ci-it-test", "testCompilerPlugin / Test / test")
addCommandAlias("ci-docs", "github; documentation / mdoc")
addCommandAlias("ci-publish", "github; ci-release")

lazy val commonSettings = Seq(
  organizationName := "Xebia Functional",
  startYear := Some(2023),
  licenses += ("Apache-2.0", new URL(
    "https://www.apache.org/licenses/LICENSE-2.0.txt"
  ))
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

lazy val testCompilerPluginLib = project
  .in(file("./munit-compiler-toolkit-testPluging-lib"))
  .settings(commonSettings)
  .settings(
    publish / skip := true
  )

lazy val testCompilerPlugin = project
  .in(file("./munit-compiler-toolkit-testPlugin"))
  .settings(commonSettings)
  .settings(
    exportJars := true,
    autoAPIMappings := true,
    publish / skip := true,
    Test / fork := true,
    libraryDependencies ++= List(
      "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
    ),
    Test / javaOptions += {
      val `scala-compiler-classpath` =
        (Compile / dependencyClasspath).value.files
          .map(_.toPath().toAbsolutePath().toString())
          .mkString(":")
      s"-Dscala-compiler-classpath=${`scala-compiler-classpath`}"
    },
    Test / javaOptions += {
      s"""-Dcompiler-scalacOptions=\"${scalacOptions.value.mkString(" ")}\""""
    },
    Test / javaOptions += Def.taskDyn {
      Def.task {
        val _ = (Compile / Keys.`package`).value
        val `scala-compiler-options` =
          s"${(Compile / packageBin).value}"
        s"""-Dscala-compiler-plugin=${`scala-compiler-options`}"""
      }
    }.value
  )
  .dependsOn(`munit-compiler-toolkit-testkit`, testCompilerPluginLib)

lazy val TestCompilerPluginIntegrationTest = project
  .in(file("./munit-compiler-toolkit-testPlugin-integration-test"))
  .settings(commonSettings)
  .settings(
    exportJars := true,
    autoAPIMappings := true,
    autoCompilerPlugins := true,
    publish / skip := true,
    Compile / fork := true,
    Test / fork := true,
    libraryDependencies += munit,
    Compile / scalacOptions += s"""-Xplugin:${(testCompilerPlugin / Compile / packageBin).value}""",
    Compile / scalacOptions += s"""-Xprint:testkittestphase""",
    Test / scalacOptions += s"""-Xplugin:${(testCompilerPlugin / Compile / packageBin).value}"""
  )
  .dependsOn(testCompilerPluginLib)

lazy val `munit-compiler-toolkit-testkit` = project
  .in(file("./munit-compiler-toolkit-testkit"))
  .settings(commonSettings)
  .settings(
    libraryDependencies += munit,
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
  )

lazy val documentation = project
  .dependsOn(`munit-compiler-toolkit-testkit`)
  .settings(commonSettings)
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))
  .settings(publish / skip := true)
