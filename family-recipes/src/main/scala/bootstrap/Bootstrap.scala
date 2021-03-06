package bootstrap

import org.slf4j.{ Logger, LoggerFactory }

trait BootStrap {

  val name: String = getClass.getSimpleName()
  val logger: Logger = LoggerFactory.getLogger(getClass)

  def initialize(): Unit

}
