import sbt._
object Dependencies {

  object Versions {
    val munit = "0.7.29"
    val sbtDependencyUpdates = "1.2.7"
    val sbtHeader = "5.9.0"
    val sbtGithub = "0.11.2"
    val sbtGithubMdoc = "0.11.2"
    val sbtMdoc = "2.3.2"
    val sbtScalafmt = "2.4.6"
  }

  object Compile {
    val munit = "org.scalameta" %% "munit" % Versions.munit
  }

  object SbtPlugins {
    val sbtDependencyUpdates =
      "org.jmotor.sbt" % "sbt-dependency-updates" % Versions.sbtDependencyUpdates
    val sbtHeader = "de.heikoseeberger" % "sbt-header" % Versions.sbtHeader
    val sbtGithub = "com.alejandrohdezma" %% "sbt-github" % Versions.sbtGithub
    val sbtGithubMdoc =
      "com.alejandrohdezma" % "sbt-github-mdoc" % Versions.sbtGithubMdoc
    val sbtMdoc = "org.scalameta" % "sbt-mdoc" % Versions.sbtMdoc
    val sbtScalafmt = "org.scalameta" % "sbt-scalafmt" % Versions.sbtScalafmt
  }

  object Test {}

  lazy val dependencies =
    Seq(Compile.munit, SbtPlugins.sbtDependencyUpdates, SbtPlugins.sbtHeader)

}
