import org.apache.kafka.streams.{KeyValue, TestInputTopic, TestOutputTopic, Topology, TopologyTestDriver}
import org.scalatest.flatspec._
import org.scalatest.matchers.should

class KafkaStreamsSpec extends AnyFlatSpec with should.Matchers {

  import org.apache.kafka.streams.scala.serialization.Serdes._

  def helper() = new {
    val topology: Topology = KafkaStream.getTopology
    val testDriver = new TopologyTestDriver(topology)

    val wordInputTopic: TestInputTopic[String, String] = testDriver.createInputTopic(KafkaStream.WORD_INPUT_TOPIC, stringSerde.serializer, stringSerde.serializer)
    val wordOutputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic(KafkaStream.WORD_OUTPUT_TOPIC, stringSerde.deserializer, stringSerde.deserializer)

    val numberInputTopic: TestInputTopic[String, String] = testDriver.createInputTopic(KafkaStream.NUMBER_INPUT_TOPIC, stringSerde.serializer, stringSerde.serializer)
    val numberOutputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic(KafkaStream.NUMBER_OUTPUT_TOPIC, stringSerde.deserializer, stringSerde.deserializer)
  }

  it should "return topology" in {
    val topology = helper()

    topology.wordInputTopic.pipeInput("", "")
    topology.wordOutputTopic.readKeyValue() shouldBe KeyValue.pair("", "")
  }

  it should "return value in uppercase" in {
    val topology = helper()

    topology.wordInputTopic.pipeInput("1", "value")
    topology.wordOutputTopic.readKeyValue() shouldBe KeyValue.pair("1", "VALUE")
  }

  it should "multiply positive numbers by a number given on command line" in {
    val topology = helper()

    topology.numberInputTopic.pipeInput("10")
    topology.numberOutputTopic.readKeyValue shouldBe KeyValue.pair(null, "20")
  }

  it should "return prefix for negative numbers" in {
    val topology = helper()

    topology.numberInputTopic.pipeInput("-2")
    topology.numberOutputTopic.readKeyValue shouldBe KeyValue.pair(null, "negative number: -2")
  }
}
