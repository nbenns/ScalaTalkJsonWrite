package json

import json.models.Json

trait JsonConvertible[A] {
  def toJson(a: A): Json
}
