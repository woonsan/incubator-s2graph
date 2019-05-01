package org.apache.s2graph.http

package object api {
  object ErrorResponse {
    def apply(message: String): ErrorResponse = {
      new ErrorResponse(Seq(message))
    }
  }

  case class ErrorResponse(messages: Seq[String])
}
