import sbt._

object Dependencies {

  val runtimeDependencies = Seq(
    "org.postgresql" % "postgresql" % "42.2.19",
    "com.opentable.components" % "otj-pg-embedded" % "0.13.3",
    "io.getquill" %% "quill-jdbc" % "3.7.0",
    "com.lihaoyi" %% "cask" % "0.7.9",
    "com.lihaoyi" %% "scalatags" % "0.9.2",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.iheart" %% "ficus" % "1.5.0",
    "commons-codec" % "commons-codec" % "1.15",
    "org.apache.commons" % "commons-lang3" % "3.12.0",
    "org.apache.commons" % "commons-email" % "1.5"
  )
}
