package org.apache.s2graph.http.api

import endpoints.akkahttp.server
import endpoints.openapi.model.{OpenApi, OpenApiSchemas}
import org.apache.s2graph.http.api.spec.AdminDocumentation

class DocumentationServer
  extends server.Endpoints
    with OpenApiSchemas
    with server.JsonSchemaEntities {

  val route = endpoint(get(path / "documentation.json"), jsonResponse[OpenApi]())
    .implementedBy(_ => AdminDocumentation.api)
}
