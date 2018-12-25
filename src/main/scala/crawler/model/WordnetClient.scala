package crawler.model

import akka.event.LoggingAdapter
import morfologik.stemming.polish.PolishStemmer
import org.jsoup.{Connection, Jsoup}

import scala.util.parsing.json.JSON

class WordnetClient(log: LoggingAdapter) {

  val stemmer = new Stemmer(new PolishStemmer)

  val WORDNET_API_URL: String = "http://plwordnet.pwr.wroc.pl/wordnet/api/lexemes/"
  val EMOTION_API_URL: String = "http://plwordnet.pwr.wroc.pl/wordnet/api/emotions/"

  val MAX_TIMEOUT_MILIS = 20000

  val REFERER_HEADER_KEY: String= "Referer"
  val REFERER_HEADER_VAL: String = "http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8"
  val IF_NONE_MATCH_KEY: String = "If-None-Match"
  val IF_NONE_MATCH_VAL: String = "\"f7ad10529224e912cac62ae39d55c7e1\""
  val REFERER_KEY: String = "Referer"
  val REFERER_VAL: String = "http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8"
  val USER_AGENT: String = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1"

  val VALUATIONS: String = "valuations"
  val EMOTIONS: String = "emotions"
  val MARKEDNESS: String = "markedness"


  def getEmotions(word: String): List[(String,String,String)] = {

    val wordnetApiLink = WORDNET_API_URL + word

    val response: Connection.Response = Jsoup.connect(wordnetApiLink)
      .timeout(MAX_TIMEOUT_MILIS)
      .header(REFERER_HEADER_KEY, REFERER_HEADER_VAL).ignoreContentType(true)
      .header(IF_NONE_MATCH_KEY, IF_NONE_MATCH_VAL)
      .header(REFERER_KEY,REFERER_VAL)
      .userAgent(USER_AGENT)
      .execute()

    val senseJSON = JSON.parseFull(response
      .parse()
      .body()
      .childNode(0) //raw json
      .toString)

    senseJSON match {
      case Some(Nil) =>
        return List(("null","null","null"))
      case Some(list: List[Map[String, Any]]) =>
        val emotionLink = EMOTION_API_URL + Option.apply(list.head("sense_id")).getOrElse()
        val emotionResponse = Jsoup.connect(emotionLink)
          .timeout(MAX_TIMEOUT_MILIS)
          .header(REFERER_HEADER_KEY, REFERER_HEADER_VAL).ignoreContentType(true)
          .header(IF_NONE_MATCH_KEY, IF_NONE_MATCH_VAL)
          .header(REFERER_KEY,REFERER_VAL)
          .userAgent(USER_AGENT)
          .execute()

        val emotionJSON = JSON.parseFull(emotionResponse
          .parse()
          .body()
          .childNode(0) //raw json
          .toString)

        emotionJSON match {
          case Some(Nil) =>
            return List(("null","null","null"))
          case Some(list: List[Map[String, Any]]) =>
            val listOfEmotions: List[(String, String, String)] = list.map(map =>
              (Option.apply(map(VALUATIONS)).getOrElse("null").toString,
                Option.apply(map(MARKEDNESS)).getOrElse("null").toString,
                  Option.apply(map(EMOTIONS)).getOrElse("null").toString))
            log.info("emotion and sense parsing complete")
            return listOfEmotions
          case _ =>
            log.error("emotion parsing error")
            return List(("null","null","null"))
        }
      case _ =>
        log.error("sense id parsing error")
        return List(("null","null","null"))
    }

  }

  def evaluateEmotions(sentence: List[String]): Markedness.Value = {
    val filteredSentence = sentence.filter(word => stemmer.checkIfAdjective(stemmer.parse(word)))
    //optymalizacja, ewaluujemy tylko przymiotniki

    val listOfEmotions: List[(String, List[(String, String, String)])] = filteredSentence.map(word => (word, getEmotions(word)))
    val reducedListOfEmotions: List[(String, (String, String, String))] = listOfEmotions.map(emotionTuple =>
      (emotionTuple._1, emotionTuple._2.reduce((a, b) =>
        (a._1 + ";" + b._1, a._2 + ";" + b._2, a._3 + ";" + b._3))))
      //change no markedness to null

    val flatMapOfMarkedness: List[String] = reducedListOfEmotions
      .flatMap(_._2._2.split(";").toList)

    if (flatMapOfMarkedness == Nil)
      return Markedness.NEUTRAL

    val numberOfNeutrals: Int = flatMapOfMarkedness.count(_.equals("null"))
    val numberOfNegatives: Int = flatMapOfMarkedness.count(_.contains("-")) //todo: add valuating strong or weak
    val numberOfPositives: Int = flatMapOfMarkedness.count(_.contains("+"))

    val maxValue = List(numberOfNeutrals, numberOfNegatives, numberOfPositives).reduceLeft(math.max)
    maxValue match {
      case `numberOfNegatives` => return Markedness.NEGATIVE
      case `numberOfPositives` => return Markedness.POSITIVE
      case `numberOfNeutrals` => List(numberOfNegatives, numberOfPositives).reduceLeft(math.max) match {
        case 0 => return Markedness.NEUTRAL
        case `numberOfPositives` => return Markedness.POSITIVE
        case `numberOfNegatives` => return Markedness.NEGATIVE
        case _ =>
          log.error("evaluating markedness failed")
          return Markedness.NEUTRAL
      }
    }

  }

}
