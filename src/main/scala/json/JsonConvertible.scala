package json

import json.models.Json

trait JsonConvertible[A] {
  type Enc <: Json
  def toJson(a: A): Enc
}
