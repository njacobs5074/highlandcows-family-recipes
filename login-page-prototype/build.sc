import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`

import mill._, scalalib._

object app extends ScalaModule {
	def scalaVersion = "2.13.4"

	override def ivyDeps = Agg(
		ivy"com.lihaoyi::cask:0.7.4",
		ivy"com.lihaoyi::scalatags:0.9.1",
		ivy"com.typesafe.scala-logging::scala-logging:3.9.2",
		ivy"ch.qos.logback:logback-classic:1.2.3"
	  )
}
