package org.apache.s2graph.http.api.spec

import endpoints.{algebra, generic}
import org.apache.s2graph.core.schema.Service


object AdminEndpoints {
  case class GetServiceResponse(serviceName: String)
}

trait AdminEndpoints
  extends algebra.Endpoints
    with algebra.JsonSchemaEntities
    with generic.JsonSchemas {

  implicit lazy val serviceSchema: JsonSchema[Service] = genericJsonSchema

  val getServiceSpec: Endpoint[String, Service] =
    endpoint(
      get(path / "getService" / segment[String]("serviceName", docs = Some("A service name."))),
      jsonResponse[Service]()
    )
}