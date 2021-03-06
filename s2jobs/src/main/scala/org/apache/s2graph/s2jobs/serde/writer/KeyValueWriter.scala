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

package org.apache.s2graph.s2jobs.serde.writer

import org.apache.hadoop.hbase.KeyValue
import org.apache.s2graph.core.{GraphElement, S2Graph}
import org.apache.s2graph.s2jobs.{DegreeKey, S2GraphHelper}
import org.apache.s2graph.s2jobs.serde.GraphElementWritable

class KeyValueWriter(autoEdgeCreate: Boolean = false,
                     skipError: Boolean = false) extends GraphElementWritable[Seq[KeyValue]] {
  override def write(s2: S2Graph)(element: GraphElement): Seq[KeyValue] = {
    try {
      S2GraphHelper.toSKeyValues(s2, element, autoEdgeCreate).map { skv =>
        new KeyValue(skv.row, skv.cf, skv.qualifier, skv.timestamp, skv.value)
      }
    } catch {
      case e: Exception =>
        if (skipError) Nil else throw e
    }
  }

  override def writeDegree(s2: S2Graph)(degreeKey: DegreeKey, count: Long): Seq[KeyValue] = {
    try {
      DegreeKey.toKeyValue(s2, degreeKey, count)
    } catch {
      case e: Exception =>
        if (skipError) Nil else throw e
    }
  }
}
