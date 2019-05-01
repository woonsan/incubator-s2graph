package org.apache.s2graph.http.api

import akka.http.scaladsl.server.Route
import endpoints.akkahttp.server
import org.apache.s2graph.core.rest.RequestParser
import org.apache.s2graph.core.{Management, S2Graph}
import org.apache.s2graph.core.schema._
import org.apache.s2graph.http.api.spec.AdminEndpoints
import org.apache.s2graph.http.api.spec.AdminEndpoints.CreateServiceRequest

import scala.util.{Success, Try}

class AdminServer(s2: S2Graph) extends AdminEndpoints with server.Endpoints with server.JsonSchemaEntities {
  val mnt = s2.management
  val parser = new RequestParser(s2)

  def getService(serviceName: String): Option[Service] = {
    Management.findService(serviceName)
  }

  def getServiceColumn(serviceColumnWithColumnName: (String, String)): Option[ServiceColumn] = {
    val (serviceName, columnName) = serviceColumnWithColumnName
    Management.findServiceColumn(serviceName, columnName)
  }

  def getLabel(labelName: String): Option[Label] = {
    Management.findLabel(labelName)
  }

  def getLabels(serviceName: String): List[Label] = {
    Management.findLabels(serviceName).toList
  }

  def listServices(): List[Service] = {
    Service.findAll()
  }

  def listLabels(): List[Label] = {
    Label.findAll()
  }

  def listServiceColumns(serviceName: String): List[ServiceColumn] = {
    Service.findByName(serviceName).map { service =>
      ServiceColumn.findByServiceId(service.id.get).toList
    }.getOrElse(Nil)
  }

  def createService(param: CreateServiceRequest): Try[Service] = {
    mnt.createService(
      serviceName = param.serviceName,
      cluster = param.cluster.getOrElse(parser.DefaultCluster),
      hTableName = param.hTableName.getOrElse(s"${param.serviceName}-${parser.DefaultPhase}"),
      preSplitSize = param.preSplitSize.getOrElse(1),
      hTableTTL = param.hTableTTL,
      compressionAlgorithm = param.compressionAlgorithm.getOrElse(parser.DefaultCompressionAlgorithm)
    )
  }

  def getRoutes: List[Route] = List(
    listServicesRoute,
    listLabelsRoute,
    listServiceColumnsRoute,
    getServiceRoute,
    getServiceColumnRoute,
    getLabelRoute,
    getLabelsRoute
  )

  val listServicesRoute = listServicesSpec.implementedBy(_ => listServices)
  val listLabelsRoute = listLabelsSpec.implementedBy(_ => listLabels)
  val listServiceColumnsRoute = listServiceColumnsSpec.implementedBy(listServiceColumns)


  val getServiceRoute = getServiceSpec.implementedBy(getService)
  val getServiceColumnRoute = getServiceColumnSpec.implementedBy(getServiceColumn)
  val getLabelRoute = getLabelSpec.implementedBy(getLabel)
  val getLabelsRoute = getLabelsSpec.implementedBy(getLabels)

  def postRoutes: List[Route] = List(
    createServiceRoute
  )

  val createServiceRoute = createServiceSpec.implementedBy(req => createService(req).toOption)
}
