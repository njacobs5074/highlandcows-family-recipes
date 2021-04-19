import Dependencies._

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

resolvers += "Lightbend Repo" at "https://repo.lightbend.com/"

// Set up FlywayDB plugin for DB migrations
// https://davidmweber.github.io/flyway-sbt-docs/index.html
enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost:5432/family_recipes"
flywayUser := System.getProperty("user.name")

addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % "4.4.8" cross CrossVersion.full)
scalacOptions += "-Yrangepos"
scalacOptions += "-deprecation"
scalacOptions += s"-P:semanticdb:sourceroot:${System.getProperty("user.dir")}"

inThisBuild(
  List(
    scalaVersion := "2.13.4",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13"
  )
)

// So that we can cleanly shutdown our service when running inside of sbt
fork in run := true

lazy val root = (project in file("."))
  .settings(
    name := "family-recipes",
    scalacOptions += "-Wunused:imports",
    libraryDependencies ++= runtimeDependencies
  )
