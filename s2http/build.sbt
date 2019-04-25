/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
lazy val akkaHttpVersion = "10.1.6"
lazy val akkaVersion = "2.5.19"
lazy val endpointVersion = "0.9.0"
lazy val circeVersion = "0.11.1"
lazy val akkaHttpJsonVersion = "1.25.2"

name := "s2http"

version := "0.1"

description := "s2graph http server"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

  "org.scalatest" %% "scalatest" % "3.0.5" % Test,

  "io.circe" %% "circe-core" % circeVersion,

  "org.julienrf" %% "endpoints-algebra" % endpointVersion,
  "org.julienrf" %% "endpoints-algebra-circe" % endpointVersion,
  "org.julienrf" %% "endpoints-algebra-json-schema" % endpointVersion,
  "org.julienrf" %% "endpoints-akka-http-server" % endpointVersion,
  "org.julienrf" %% "endpoints-json-schema-generic" % endpointVersion,
  "org.julienrf" %% "endpoints-openapi" % endpointVersion,
  "de.heikoseeberger" %% "akka-http-circe" % akkaHttpJsonVersion,

  "org.webjars" % "swagger-ui" % "3.20.9"

)

Revolver.settings
