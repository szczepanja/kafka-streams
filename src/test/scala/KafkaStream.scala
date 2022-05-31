import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

import java.util.Properties

object KafkaStream {

  val INPUT_TOPIC = "stream-input"
  val OUTPUT_TOPIC = "stream-output"

  def main(args: Array[String]): Unit = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ":9092")
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

    val source: KStream[String, String] = builder.stream[String, String](INPUT_TOPIC)

    val wordsToUpper = source.mapValues(value => {
      value.toUpperCase
    })

    val processedValues = wordsToUpper.mapValues(value => {
      value
    })

    processedValues.to(OUTPUT_TOPIC)
    builder.build()
  }

}
