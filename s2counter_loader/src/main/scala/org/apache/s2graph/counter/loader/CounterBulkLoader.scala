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

package org.apache.s2graph.counter.loader

import org.apache.s2graph.core.GraphUtil
import org.apache.s2graph.counter.config.S2CounterConfig
import org.apache.s2graph.counter.core.BlobExactKey
import org.apache.s2graph.counter.loader.config.StreamingConfig
import org.apache.s2graph.counter.loader.core.{CounterFunctions, CounterEtlFunctions}
import org.apache.s2graph.counter.models.Counter.ItemType
import org.apache.s2graph.counter.models.{DBModel, CounterModel}
import org.apache.s2graph.spark.config.S2ConfigFactory
import org.apache.s2graph.spark.spark.{HashMapParam, SparkApp, WithKafka}
import org.apache.spark.SparkContext

import scala.collection.mutable.{HashMap => MutableHashMap}
import scala.concurrent.ExecutionContext

object CounterBulkLoader extends SparkApp with WithKafka {
   lazy val config = S2ConfigFactory.config
   lazy val s2Config = new S2CounterConfig(config)
   lazy val counterModel = new CounterModel(config)
   lazy val className = getClass.getName.stripSuffix("$")
   lazy val producer = getProducer[String, String](StreamingConfig.KAFKA_BROKERS)

   implicit val ec = ExecutionContext.Implicits.global

   val initialize = {
     println("initialize")
 //    Graph(config)
     DBModel.initialize(config)
     true
   }

   override def run(): Unit = {
     val hdfsPath = args(0)
     val blockSize = args(1).toInt
     val minPartitions = args(2).toInt
     val conf = sparkConf(s"$hdfsPath: CounterBulkLoader")

     val sc = new SparkContext(conf)
     val acc = sc.accumulable(MutableHashMap.empty[String, Long], "Throughput")(HashMapParam[String, Long](_ + _))

     val msgs = sc.textFile(hdfsPath)

     val etlRdd = msgs.repartition(minPartitions).mapPartitions { part =>
       // parse and etl
       assert(initialize)
       val items = {
         for {
           msg <- part
           line <- GraphUtil.parseString(msg)
           sp = GraphUtil.split(line) if sp.size <= 7 || GraphUtil.split(line)(7) != "in"
           item <- CounterEtlFunctions.parseEdgeFormat(line)
         } yield {
           acc += (("Edges", 1))
           item
         }
       }
       items.grouped(blockSize).flatMap { grouped =>
         grouped.groupBy(e => (e.service, e.action)).flatMap { case ((service, action), v) =>
           CounterEtlFunctions.checkPolicyAndMergeDimension(service, action, v.toList)
         }
       }
     }

     val exactRdd = CounterFunctions.exactCountFromEtl(etlRdd, etlRdd.partitions.length)
     val logRdd = exactRdd.mapPartitions { part =>
       val seq = part.toSeq
       CounterFunctions.insertBlobValue(seq.map(_._1).filter(_.itemType == ItemType.BLOB).map(_.asInstanceOf[BlobExactKey]), acc)
       // update exact counter
       CounterFunctions.updateExactCounter(seq, acc).toIterator
     }

     val rankRdd = CounterFunctions.makeRankingRddFromTrxLog(logRdd, logRdd.partitions.length)
     rankRdd.foreachPartition { part =>
       CounterFunctions.updateRankingCounter(part, acc)
     }
   }
 }
