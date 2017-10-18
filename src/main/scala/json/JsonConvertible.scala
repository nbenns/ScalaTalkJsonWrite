package json

import json.models.Json

trait JsonConvertible {
  def toJson: Json
}
