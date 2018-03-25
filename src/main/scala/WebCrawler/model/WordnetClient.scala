package WebCrawler.model

import akka.event.LoggingAdapter
import org.jsoup.{Connection, Jsoup}

import scala.util.parsing.json.JSON

class WordnetClient(log: LoggingAdapter) {

  val WORDNET_API_URL: String = "http://plwordnet.pwr.wroc.pl/wordnet/api/lexemes/"
  val EMOTION_API_URL: String = "http://plwordnet.pwr.wroc.pl/wordnet/api/emotions/"

  val MAX_TIMEOUT_MILIS = 10000

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
      case Some(list: List[Map[String, Any]]) =>
        val emotionLink = EMOTION_API_URL + list.head("sense_id")
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
          case Some(list: List[Map[String, Any]]) =>
            val listOfEmotions: List[(String, String, String)] = list.map(map => (map(VALUATIONS).toString,
              map(MARKEDNESS).toString, map(EMOTIONS).toString))
            listOfEmotions
          case _ =>
            log.error("emotion parsing error")
            List(("","",""))
        }
      case _ => log.error("sense id parsing error")
        List(("","",""))
    }

  }

  def valuateEmotions(sentence: List[String]) = {
    val listOfEmotions: List[(String, List[(String, String, String)])] = sentence.map(word => (word, getEmotions(word)))

  }

}
