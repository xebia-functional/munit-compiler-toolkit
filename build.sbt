import Dependencies.Compile._

ThisBuild / scalaVersion := "3.2.2"
ThisBuild / organization := "com.xebia"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / version := "0.3.0"

addCommandAlias(
  "ci-test",
  "scalafmtCheckAll; scalafmtSbtCheck; github; documentation / mdoc; root / test"
)
addCommandAlias(
  "ci-it-test",
  "root / test; TestCompilerPluginIntegrationTest / run"
)
addCommandAlias("ci-docs", "github; documentation / mdoc")
addCommandAlias("ci-publish", "github; ci-release")

lazy val commonSettings = Seq(
  organizationName := "Xebia Functional",
  startYear := Some(2023),
  licenses += ("Apache-2.0", new URL(
    "https://www.apache.org/licenses/LICENSE-2.0.txt"
  ))
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "munit-compiler-toolkit-root",
    publish / skip := true
  )
  .aggregate(
    `munit-compiler-toolkit`,
    testCompilerPlugin,
    testCompilerPluginLib,
    TestCompilerPluginIntegrationTest
  )

lazy val testCompilerPluginLib = project
  .in(file("./munit-compiler-toolkit-testPluging-lib"))
  .settings(commonSettings)
  .settings(
    publish / skip := true
  )

lazy val testCompilerPlugin = project
  .in(file("./munit-compiler-toolkit-testPlugin"))
  .dependsOn(`munit-compiler-toolkit`, testCompilerPluginLib)
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
  .dependsOn(testCompilerPluginLib, testCompilerPlugin)

lazy val `munit-compiler-toolkit` = project
  .in(file("./munit-compiler-toolkit"))
  .settings(commonSettings)
  .settings(
    libraryDependencies += munit,
    libraryDependencies += "org.scala-lang" %% "scala3-compiler" % scalaVersion.value
  )

lazy val documentation = project
  .dependsOn(`munit-compiler-toolkit`)
  .settings(commonSettings)
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))
  .settings(publish / skip := true)
