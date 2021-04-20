package util.helpers

import play.api.libs.json.{ JsValue, Json, Writes }

package object jsonExt {
  implicit class JsonExt[T](data: T)(implicit writes: Writes[T]) {
    def asJson: JsValue = Json.toJson(data)
  }

  implicit class JsValueExt(json: JsValue) {
    def asString: String = json.toString()
  }

}
