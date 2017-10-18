package json

import json.models.Json

trait JsonConvertable {
  def toJson: Json
}
