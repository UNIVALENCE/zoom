package models

import java.time.Instant
import java.util.UUID

import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import utils.RandomizePostKafka
import zoom._
import callsite.CallSiteInfo
import zoom.model.{BuildInfo, Environment, StartedNewNode}

object BuildInfoTest {

  val buildInfo: BuildInfo = {
    val cs = implicitly[CallSiteInfo]

    BuildInfo(
      name = "",
      organization = "",
      version = "",
      commit = cs.commit,
      buildAt = Instant.ofEpochMilli(cs.buildAt)
    )
  }

  val startedNewNode: StartedNewNode =
    StartedNewNode.fromBuild(BuildInfoTest.buildInfo, Environment.Production, UUID.randomUUID())

}

class StartedNewNodeTest extends FunSuite with EmbdedKafkaCustom with EmbeddedKafka with BeforeAndAfterAll {

  implicit val embdedKafkaConfig: EmbeddedKafkaConfig =
    RandomizePostKafka.changePortKafkaConfiguration_!(EmbeddedKafkaConfig.defaultConfig)

  val testKafkaConfiguration =
    KafkaConfiguration(embdedKafkaConfig.kafkaPort, "localhost")

  implicit val keySerializer: StringSerializer  = new StringSerializer
  implicit val stringDe: StringDeserializer     = new StringDeserializer
  implicit val byteArrDe: ByteArrayDeserializer = new ByteArrayDeserializer

  override def beforeAll(): Unit = {
    EmbeddedKafka.stop()
    EmbeddedKafka.start
  }

  override def afterAll(): Unit = {
    EmbeddedKafka.stop()
  }

  test("testFromBuild") {
    import BuildInfoTest._

    val inJson = ZoomEventSerde.toJson(startedNewNode)

    assert(inJson.event_type == "zoom.model.StartedNewNode")

    assert(inJson.payload.contains("StartedNewNode"))

    assert(
      ZoomEventSerde
        .fromJson[StartedNewNode](inJson.payload)
        .get == startedNewNode)

  }

  test("Publish To Node") {

    implicit val buildInfo = BuildInfoTest.buildInfo
    //implicit val nc = new NodeContext(environment = Environment.Local)

    //val fm = new String(consumeFirstMessageFrom[Array[Byte]]("data.event"))
    /*val fromJson = EventSerde.fromJson[StartedNewNode](fm)

    assert(fromJson.isSuccess)*/

    /*val event = fromJson.get
    assert(event.prg_name == buildInfo.name)
    assert(event.environment == Environment.Recette)
    assert(event.node_hostname.nonEmpty)*/

  }

}
