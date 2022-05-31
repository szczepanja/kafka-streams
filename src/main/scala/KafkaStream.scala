import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import java.util.Properties

object KafkaStream {

  val WORD_INPUT_TOPIC = "word-input"
  val WORD_OUTPUT_TOPIC = "word-output"

  def main(args: Array[String]): Unit = {
    val bootstrapServers = sys.env.getOrElse("BOOTSTRAP_SERVERS", ":9092")
    val appIdConfig = sys.env.getOrElse("APP_ID", "kafka-streams")

    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, appIdConfig)
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.getClass)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.getClass)

    val topology = getTopology
    val stream = new KafkaStreams(topology, props)
    stream.start()
  }

  def getTopology: Topology = {
    val builder = new StreamsBuilder()
    import org.apache.kafka.streams.scala.ImplicitConversions._
    import org.apache.kafka.streams.scala.serialization.Serdes._

    val source: KStream[String, String] = builder.stream[String, String](WORD_INPUT_TOPIC)

    val wordsToUpper = source.mapValues(value => {
      value.toUpperCase
    })

    wordsToUpper.to(WORD_OUTPUT_TOPIC)
    builder.build()
  }

}
