package org.apache.s2graph.http.api.spec

import endpoints.openapi
import endpoints.openapi.model.{Info, OpenApi}

object AdminDocumentation
  extends AdminEndpoints
    with openapi.Endpoints
    with openapi.JsonSchemaEntities {

  val api: OpenApi =
    openApi(
      Info(title = "API to manipulate a counter", version = "1.0.0")
    )(getServiceSpec)
}
