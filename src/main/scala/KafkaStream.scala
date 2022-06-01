import Parser.getParser
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}
import scopt.OParser

import java.util.Properties

object KafkaStream {

  def main(args: Array[String]): Unit = {
    OParser.parse(getParser, args, Config()) match {
      case Some(config) => {
        val bootstrapServers = sys.env.getOrElse("BOOTSTRAP_SERVERS", ":9092")
        val appIdConfig = sys.env.getOrElse("APP_ID", "kafka-streams")

        val props = new Properties()
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appIdConfig)
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.getClass)
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.getClass)

        val topology = getTopology(config)
        val stream = new KafkaStreams(topology, props)
        stream.start()
      }
      case None => println("Wrong configuration")
    }
  }

  def getTopology(c: Config): Topology = {
    val builder = new StreamsBuilder()
    import org.apache.kafka.streams.scala.ImplicitConversions._
    import org.apache.kafka.streams.scala.serialization.Serdes._

    def prefix(negative: Int) = s"negative number: " + negative

    val words: KStream[String, String] = builder.stream[String, String](c.wordsInput)

    val wordsToUpper = words.mapValues(value => {
      value.toUpperCase
    })

    val numbers: KStream[String, String] = builder.stream[String, String](c.multiplierInput)

    def multiply(value: Int) = value match {
      case v if v > 0 => v * 2
      case _ => prefix(value)
    }

    val multiplyNumbers = numbers.mapValues(value =>
      multiply(value.toInt).toString)

    wordsToUpper.to(c.wordsOutput)
    multiplyNumbers.to(c.multiplierOutput)

    builder.build()
  }

}
