object Parser {

  def getParser = {
    import scopt.OParser
    val builder = OParser.builder[Config]

    val parser = {
      import builder._
      OParser.sequence(
        opt[String]('i', "words-input")
          .action((name, c) => c.copy(wordsInput = name))
          .text("Input topic for words"),
        opt[String]('o', "words-output")
          .action((name, c) => c.copy(wordsOutput = name))
          .text("Output topic for words")
      )
    }
    parser
  }

}
