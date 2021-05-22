import Dependencies._

resolvers ++= Seq(
  "Typesafe" at "https://repo.typesafe.com/typesafe/releases"
)

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

// Set up FlywayDB plugin for DB migrations
// https://davidmweber.github.io/flyway-sbt-docs/index.html
enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost:5432/family_recipes"
flywayUser := System.getProperty("user.name")

addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % "4.4.8" cross CrossVersion.full)
scalacOptions += "-Yrangepos"
scalacOptions += "-deprecation"
scalacOptions += "-feature"
scalacOptions += s"-P:semanticdb:sourceroot:${System.getProperty("user.dir")}"

inThisBuild(
  List(
    scalaVersion := "2.13.4",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13"
  )
)

PlayKeys.devSettings += "play.server.http.port" -> "8080"

run / fork := true

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-family-recipes",
    scalacOptions += "-Wunused:imports",
    libraryDependencies += guice,
    libraryDependencies ++= runtimeDependencies,
    libraryDependencies ++= testDependencies
  )
