import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import $ivy.`com.goyeau::mill-scalafix:0.2.1`

import com.goyeau.mill.scalafix.ScalafixModule

import mill._, scalalib._

object app extends ScalaModule with ScalafixModule {
  def scalaVersion = "2.13.4"

  def scalacOptions = Seq("-Wunused")

  override def ivyDeps =
    Agg(
      ivy"com.lihaoyi::cask:0.7.8",
      ivy"com.lihaoyi::scalatags:0.9.2",
      ivy"com.typesafe.scala-logging::scala-logging:3.9.2",
      ivy"ch.qos.logback:logback-classic:1.2.3"
    )

  def scalafixScalaBinaryVersion = "2.13"

  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:0.5.0-alpha.1")
}
