import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest._
import org.apache.commons.lang3.RandomStringUtils

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

package object util {

  implicit class StringExt(s: String) {
    def secureHash(secretKey: String): String = {
      val hmac: Array[Byte] = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes).hmac(s.getBytes)
      Base64.encodeBase64URLSafeString(hmac)
    }

    def decodeBase64(): String = new String(Base64.decodeBase64(s))

  }

  implicit class ThrowableExt(t: Throwable) {
    def getMessageOrName: String = Option(t.getMessage).getOrElse(t.getClass.getSimpleName)
  }

  implicit class FutureExt[T](f: Future[T]) {
    def asTry(implicit executionContext: ExecutionContext): Future[Try[T]] = {
      f.map(Success(_): Try[T]).recover { case t => Failure(t) }
    }
  }

  def generateTempPassword(len: Int = 20): String = RandomStringUtils.randomPrint(len)
}
