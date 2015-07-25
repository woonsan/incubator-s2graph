package com.daumkakao.s2graph.rest.config

import java.util.concurrent.TimeUnit

import com.codahale.metrics.{Metric, MetricRegistry, Slf4jReporter}
import org.slf4j.LoggerFactory
import play.api.Play

object Config {


  // HBASE
  lazy val HBASE_ZOOKEEPER_QUORUM = conf.getString("hbase.zookeeper.quorum").getOrElse("localhost")

  // HBASE CLIENT
  lazy val ASYNC_HBASE_CLIENT_FLUSH_INTERVAL = conf.getInt("async.hbase.client.flush.interval").getOrElse(1000).toShort
  lazy val RPC_TIMEOUT = conf.getInt("hbase.client.operation.timeout").getOrElse(1000)
  lazy val MAX_ATTEMPT = conf.getInt("hbase.client.operation.maxAttempt").getOrElse(3)

  // PHASE
  lazy val PHASE = conf.getString("phase").getOrElse("dev")
  lazy val conf = Play.current.configuration

  // CACHE
  lazy val CACHE_TTL_SECONDS = conf.getInt("cache.ttl.seconds").getOrElse(600)
  lazy val CACHE_MAX_SIZE = conf.getInt("cache.max.size").getOrElse(10000)

  //KAFKA
  lazy val KAFKA_METADATA_BROKER_LIST = conf.getString("kafka.metadata.broker.list").getOrElse("localhost")
  lazy val KAFKA_PRODUCER_POOL_SIZE = conf.getInt("kafka.producer.pool.size").getOrElse(0)
  lazy val KAFKA_LOG_TOPIC = s"s2graphIn${PHASE}"
  lazy val KAFKA_FAIL_TOPIC = s"s2graphIn${PHASE}Failed"

  // use Keep-Alive
  lazy val USE_KEEP_ALIVE = conf.getBoolean("use.keep.alive").getOrElse(false)

  // is query or write
  lazy val IS_QUERY_SERVER = conf.getBoolean("is.query.server").getOrElse(true)
  lazy val IS_WRITE_SERVER = conf.getBoolean("is.write.server").getOrElse(true)


  // query limit per step
  lazy val QUERY_HARD_LIMIT = conf.getInt("query.hard.limit").getOrElse(300)

  val metricRegistry = new com.codahale.metrics.MetricRegistry()
}

trait Instrumented extends nl.grons.metrics.scala.InstrumentedBuilder  {
  val metricRegistry: MetricRegistry = Config.metricRegistry
  val reporter = Slf4jReporter.forRegistry(metricRegistry)
    .outputTo(LoggerFactory.getLogger(classOf[Instrumented]))
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()
  //  val consoleReporter = ConsoleReporter.forRegistry(metricRegistry)
  //    .convertRatesTo(TimeUnit.SECONDS)
  //    .convertDurationsTo(TimeUnit.MILLISECONDS)
  //    .build()

  val stats = new collection.mutable.HashMap[String, Metric]

  /**
   * Edge
   */
  // insert
  def getOrElseUpdateMetric[M <: Metric](key: String)(op: => M)= {
    stats.get(key) match {
      case None =>
        val m = op
        stats += (key -> m)
        m.asInstanceOf[M]
      case Some(m) => m.asInstanceOf[M]
    }
  }
}
