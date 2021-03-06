import net.ceedubs.ficus.Ficus._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest._

package object util {

  def secureHash(s: String): String = {
    val secretKey = app.config.as[String]("secretKey")

    val hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes).hmac(s.getBytes)
    Base64.encodeBase64URLSafeString(hmac)
  }

}
