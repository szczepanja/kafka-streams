import org.apache.kafka.streams.{KeyValue, TestInputTopic, TestOutputTopic, Topology, TopologyTestDriver}
import org.scalatest.flatspec._
import org.scalatest.matchers.should

class KafkaStreamsSpec extends AnyFlatSpec with should.Matchers {

  import org.apache.kafka.streams.scala.serialization.Serdes._

  def helper() = new {
    val topology: Topology = KafkaStream.getTopology
    val testDriver = new TopologyTestDriver(topology)
    val inputTopic: TestInputTopic[String, String] = testDriver.createInputTopic(KafkaStream.INPUT_TOPIC, stringSerde.serializer, stringSerde.serializer())
    val outputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic(KafkaStream.OUTPUT_TOPIC, stringSerde.deserializer, stringSerde.deserializer)
  }

  it should "return topology" in {
    val topology = helper()

    topology.inputTopic.pipeInput("", "")
    topology.outputTopic.readKeyValue() shouldBe KeyValue.pair("", "")
  }


  it should "return value in uppercase" in {
    val topology = helper()

    topology.inputTopic.pipeInput("1", "value")
    topology.outputTopic.readKeyValue() shouldBe KeyValue.pair("1", "VALUE")
  }

}
