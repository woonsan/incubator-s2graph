//package org.apache.s2graph.http.api.spec
//
//import io.circe.Decoder.Result
//import io.circe._
//import org.joda.time.format.ISODateTimeFormat
//
//
//trait DataTypeSupport {
//
//  import io.circe.parser.parse
//  import spray.json._
//
//  implicit val sprayJsonEncode: Encoder[JsValue] = new Encoder[JsValue] {
//    override def apply(in: JsValue): Json = parse(in.toString()).right.get
//  }
//
//  implicit val sprayJsonDecoder: Decoder[JsValue] = new Decoder[JsValue] {
//    override def apply(c: HCursor): Result[JsValue] = try {
//      Right(c.value.noSpaces.parseJson)
//    } catch {
//      case e: Exception => Left(DecodingFailure("Json parse failed", c.history))
//    }
//  }
//
//  implicit val schemaForSprayJson: JsonSchema[JsValue] = new JsonSchema[JsValue] {
//    override def schema: JsonSchema = {
//      SObject(
//        SObjectInfo("JsonValue", "Json Value"),
//        Seq(),
//        Seq()
//      )
//    }
//  }
//
//  // Joda Time
//  import org.joda.time.DateTime
//
//  implicit val dateTimeEncode: Encoder[DateTime] = new Encoder[DateTime] {
//    override def apply(in: DateTime): Json = {
//      val str = in.toString(ISODateTimeFormat.dateTime())
//      Json.fromString(str)
//    }
//  }
//
//  implicit val dateTimeDecoder: Decoder[DateTime] = new Decoder[DateTime] {
//    override def apply(c: HCursor): Result[DateTime] = try {
//      Right(DateTime.parse(c.value.noSpaces, ISODateTimeFormat.dateTime))
//    } catch {
//      case e: Exception => Left(DecodingFailure("Json parse failed", c.history))
//    }
//  }
//
//  implicit val schemaForDateTime: SchemaFor[DateTime] = new SchemaFor[DateTime] {
//    override def schema: Schema = SString
//  }
//}
