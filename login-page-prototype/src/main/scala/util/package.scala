import com.typesafe.config.{ Config, ConfigFactory }
import net.ceedubs.ficus.Ficus._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest._

import java.util.UUID

package object util {

  case class ApiError(statusCode: Int, statusText: Option[String] = None, data: Option[Any] = None)
      extends Exception(statusText.orNull)

  def secureHash(s: String): String = {
    val config: Config = ConfigFactory.load()
    val secretKey = config.as[String]("secretKey")

    val hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes).hmac(s.getBytes)
    Base64.encodeBase64URLSafeString(hmac)
  }

  def newSessionKey(): String = UUID.randomUUID().toString()

}
