package org.apache.s2graph.http.api.spec

import endpoints.{algebra, generic}
import org.apache.s2graph.core.schema._


/**
  * place for request/response related to admin APIs.
  */
object AdminEndpoints {

  case class CreateServiceRequest(serviceName: String,
                                  cluster: Option[String],
                                  hTableName: Option[String],
                                  preSplitSize: Option[Int],
                                  hTableTTL: Option[Int],
                                  compressionAlgorithm: Option[String])

}

trait AdminEndpoints
  extends algebra.Endpoints
    with algebra.JsonSchemaEntities
    with generic.JsonSchemas {

  import AdminEndpoints._

  implicit lazy val serviceSchema: JsonSchema[Service] = genericJsonSchema
  implicit lazy val serviceColumnSchema: JsonSchema[ServiceColumn] = genericJsonSchema
  implicit lazy val labelSchema: JsonSchema[Label] = genericJsonSchema
  implicit lazy val createServiceSchema: JsonSchema[CreateServiceRequest] = genericJsonSchema

  def getSpecs: List[Endpoint[_, _]] = {
    List(
      listServicesSpec,
      listLabelsSpec,
      listServiceColumnsSpec,
      getServiceSpec,
      getServiceColumnSpec,
      getLabelSpec,
      getLabelsSpec
    )
  }


  val listServicesSpec: Endpoint[Unit, List[Service]] =
    endpoint(
      get(path
        / "listServices"
      ),
      jsonResponse[List[Service]]()
    )

  val listLabelsSpec: Endpoint[Unit, List[Label]] =
    endpoint(
      get(path
        / "listLabels"
      ),
      jsonResponse[List[Label]]()
    )

  val listServiceColumnsSpec: Endpoint[String, List[ServiceColumn]] =
    endpoint(
      get(path
        / "listServiceColumns"
        / segment[String]("serviceName", docs = Some("A service name."))
      ),
      jsonResponse[List[ServiceColumn]]()
    )

  val getServiceSpec: Endpoint[String, Option[Service]] =
    endpoint(
      get(path
        / "getService"
        / segment[String]("serviceName", docs = Some("A service name."))
      ),
      jsonResponse[Service]().orNotFound()
    )

  val getServiceColumnSpec: Endpoint[(String, String), Option[ServiceColumn]] =
    endpoint(
      get(path
        / "getServiceColumn"
        / segment[String]("serviceName", docs = Some("A service name,"))
        / segment[String]("columnName", docs = Some("A column name."))
      ),
      jsonResponse[ServiceColumn]().orNotFound()
    )

  val getLabelSpec: Endpoint[String, Option[Label]] =
    endpoint(
      get(path
        / "getLabel"
        / segment[String]("labelName", docs = Some("A label name."))
      ),
      jsonResponse[Label]().orNotFound()
    )

  val getLabelsSpec: Endpoint[String, List[Label]] =
    endpoint(
      get(path
        / "getLabels"
        / segment[String]("serviceName", docs = Some("A service name."))
      ),
      jsonResponse[List[Label]]()
    )

  def postSpecs = List(
    createServiceSpec
  )

  //TODO: this should return when exception occur so
  // response type should be Try or, Either[ErrorResponse, Service]
  // not sure yet how to work with Try/Either type with specs.
  val createServiceSpec: Endpoint[CreateServiceRequest, Option[Service]] =
  endpoint(
    post(path / "createService",
      jsonRequest[CreateServiceRequest]()
    ), jsonResponse[Service]().orNotFound()
  )

}