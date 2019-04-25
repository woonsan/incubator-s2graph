package org.apache.s2graph.http.api

import endpoints.akkahttp.server
import endpoints.algebra.Endpoints
import org.apache.s2graph.core.Management
import org.apache.s2graph.core.schema.Service
import org.apache.s2graph.http.api.spec.AdminEndpoints


class AdminServer extends AdminEndpoints with server.Endpoints with server.JsonSchemaEntities {

  def getService(serviceName: String): Service = {
    val serviceOpt = Management.findService(serviceName)
    serviceOpt.get
  }

  val route = getServiceSpec.implementedBy(getService)
}
