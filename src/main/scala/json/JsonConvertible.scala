package json

import json.models.Json

trait JsonConvertible[A] {
  type Enc <: Json
  def toJson(a: A): Enc
}

object JsonConvertible {
  type Aux[In, Out] = JsonConvertible[In] { type Enc = Out }
}
