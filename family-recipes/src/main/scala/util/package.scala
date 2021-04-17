import net.ceedubs.ficus.Ficus._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest._
import play.api.libs.json.{ JsValue, Json, Writes }

import scala.language.implicitConversions

package object util {

  def secureHash(s: String): String = {
    val secretKey = app.config.as[String]("secretKey")

    val hmac: Array[Byte] = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes).hmac(s.getBytes)
    Base64.encodeBase64URLSafeString(hmac)
  }

  implicit class JsonExt[T](data: T)(implicit writes: Writes[T]) {
    def asJson: JsValue = Json.toJson(data)
  }

  implicit class JsValueExt(json: JsValue) {
    def asString: String = json.toString()
  }

  implicit def asString(json: JsValue): String = json.toString
}
