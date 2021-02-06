ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.8",
  "io.getquill" %% "quill-jdbc" % "3.5.2",
  "io.getquill" %% "quill-codegen-jdbc" % "3.6.0",
  "com.lihaoyi" %% "cask" % "0.7.8",
  "com.lihaoyi" %% "scalatags" % "0.9.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost:5432/family_recipes"
flywayUser := System.getProperty("user.name")
flywayPassword := ""
flywayLocations += "filesystem:conf/migrations"

lazy val root = (project in file("."))
  .settings(
    name := "login-page-prototype"
  )
