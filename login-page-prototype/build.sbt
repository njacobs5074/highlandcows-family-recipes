ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.8",
  "com.opentable.components" % "otj-pg-embedded" % "0.13.1",
  "io.getquill" %% "quill-jdbc" % "3.5.2",
  "com.lihaoyi" %% "cask" % "0.7.8",
  "com.lihaoyi" %% "scalatags" % "0.9.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.iheart" %% "ficus" % "1.5.0",
  "commons-codec" % "commons-codec" % "1.15",
  "org.apache.commons" % "commons-lang3" % "3.11"
)

// Set up FlywayDB plugin for DB migrations
// https://davidmweber.github.io/flyway-sbt-docs/index.html
enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost:5432/family_recipes"
flywayUser := System.getProperty("user.name")
flywayLocations += "filesystem:conf/migrations"

addCompilerPlugin("org.scalameta" % "semanticdb-scalac" % "4.4.8" cross CrossVersion.full)
scalacOptions += "-Yrangepos"
scalacOptions += "-deprecation"

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
    name := "login-page-prototype",
    scalacOptions += "-Wunused:imports"
  )
