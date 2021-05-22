import sbt._

object Dependencies {
  val runtimeDependencies = Seq(
    "org.postgresql" % "postgresql" % "42.2.19",
    "org.flywaydb" %% "flyway-play" % "7.8.0",
    "com.typesafe.play" %% "play-slick" % "5.0.0",
    "com.github.tminglei" %% "slick-pg_play-json" % "0.19.6",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "commons-codec" % "commons-codec" % "1.15",
    "org.apache.commons" % "commons-lang3" % "3.12.0",
    "org.apache.commons" % "commons-email" % "1.5"
  )

  val testDependencies = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
  )
}
