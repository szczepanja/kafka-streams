import org.apache.kafka.streams.{KeyValue, TestInputTopic, TestOutputTopic, Topology, TopologyTestDriver}
import org.scalatest.flatspec._
import org.scalatest.matchers.should

class KafkaStreamsSpec extends AnyFlatSpec with should.Matchers {

  val WORD_INPUT_TOPIC = "word-input"
  val WORD_OUTPUT_TOPIC = "word-output"

  val NUMBER_INPUT_TOPIC = "number-input"
  val NUMBER_OUTPUT_TOPIC = "number-output"

  val config = Config(WORD_INPUT_TOPIC, WORD_OUTPUT_TOPIC, NUMBER_INPUT_TOPIC, NUMBER_OUTPUT_TOPIC)

  import org.apache.kafka.streams.scala.serialization.Serdes._

  def helper(config: Config) = new {
    val topology: Topology = KafkaStream.getTopology(config)
    val testDriver = new TopologyTestDriver(topology)

    val wordInputTopic: TestInputTopic[String, String] = testDriver.createInputTopic(config.wordsInput, stringSerde.serializer, stringSerde.serializer)
    val wordOutputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic(config.wordsOutput, stringSerde.deserializer, stringSerde.deserializer)

    val numberInputTopic: TestInputTopic[String, String] = testDriver.createInputTopic(config.multiplierInput, stringSerde.serializer, stringSerde.serializer)
    val numberOutputTopic: TestOutputTopic[String, String] = testDriver.createOutputTopic(config.multiplierOutput, stringSerde.deserializer, stringSerde.deserializer)
  }

  it should "return topology" in {
    val topology = helper(config)

    topology.wordInputTopic.pipeInput("", "")
    topology.wordOutputTopic.readKeyValue() shouldBe KeyValue.pair("", "")
  }

  it should "return value in uppercase" in {
    val topology = helper(config)

    topology.wordInputTopic.pipeInput("1", "value")
    topology.wordOutputTopic.readKeyValue() shouldBe KeyValue.pair("1", "VALUE")
  }

  it should "multiply positive numbers by a number given on command line" in {
    val topology = helper(config)

    topology.numberInputTopic.pipeInput("10")
    topology.numberOutputTopic.readKeyValue shouldBe KeyValue.pair(null, "20")
  }

}
