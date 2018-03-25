package WebCrawler

import WebCrawler.actors.Supervisor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import org.jsoup.{Connection, Jsoup}

import scala.language.postfixOps
import scala.util.parsing.json.JSON


/**
  * Created by apiotrowski on 14.10.2017.
  */

object Main extends App {

  //  val stemmer = new PolishStemmer
//
//  val in = "Nie zabrakło oczywiście wpadek. " +
//    "Największym zaskoczeniem okazał się dla nas strój Katarzyny Zielińskiej, " +
//    "której ewidentnie o coś chodziło, " +
//    "ale wciąż nie wiemy o co."
//
//  val splitted = in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+")
//
//  for ( t <- in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+") ) {
//    println("> '" + t + "'")
//      stemmer.lookup(t)
//        .forEach(wd => {
//            print("  - " +
//              (if (wd.getStem == null) "<null>"
//              else wd.getStem) + ", " + wd.getTag)
//          }
//        )
//    println()
//  }

//  val stemmer = new Stemmer(new PolishStemmer(), "rysy")
//  println(stemmer.keywordPredicate("Pogoda na rysy jest kiepska"))
//  println(stemmer.keywordPredicate("xd Pogoda na rysy jest kiepska xd"))
//  println(stemmer.keywordPredicate("xD Pogoda na rysach jest słaba xD"))
//  println(stemmer.keywordPredicate("Pogoda na rysach jest kiepska"))
//  println(stemmer.keywordPredicate("na telefonie mam rysy xD"))

  val keyWord = "słaby"

  implicit val system: ActorSystem = ActorSystem()

  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))

  val log = Logging(system, supervisor)

  val link = "http://plwordnet.pwr.wroc.pl/wordnet/api/lexemes/" + keyWord
  val emotionsDomain = "http://plwordnet.pwr.wroc.pl/wordnet/api/emotions/"

  val response: Connection.Response = Jsoup.connect(link)
    .timeout(10000)
    .header("Referer","http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8").ignoreContentType(true)
    .header("If-None-Match","\"f7ad10529224e912cac62ae39d55c7e1\"")
    .header("Referer","http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8")
    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
    .execute()

  val doc = response.parse().body().childNode(0).toString

  val senseJSON = JSON.parseFull(doc)

  senseJSON match {
    case Some(list: List[Map[String, Any]]) =>
      val emotionLink = emotionsDomain + list.head("sense_id")
      val emotionResponse = Jsoup.connect(emotionLink)
        .timeout(10000)
        .header("Referer","http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8").ignoreContentType(true)
        .header("If-None-Match","\"f7ad10529224e912cac62ae39d55c7e1\"")
        .header("Referer","http://plwordnet.pwr.wroc.pl/wordnet/7c93b054-2081-11e8-b33d-8bb6af2a20b8")
        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
        .execute()
      val rawJSON = emotionResponse.parse().body().childNode(0).toString
      val emotionJSON = JSON.parseFull(rawJSON)
      emotionJSON match {
        case Some(list: List[Map[String, Any]]) =>
          val listOfInfo = list.map(map => (map("valuations"), map("markedness"), map("emotions")))
          println(list)
        case _ => log.error("emotion parsing error")
      }
    case _ => log.error("sense id parsing error")
  }

  println(doc)





//
//  implicit val system: ActorSystem = ActorSystem()
//
//  val keyWord: String = args(0)
//  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))
//
//  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))
//
//  Await.result(system.whenTerminated, 5 hours)
//
//  supervisor ! PoisonPill
//  system.terminate

}
