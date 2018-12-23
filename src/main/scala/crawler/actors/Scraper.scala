package crawler.actors

import java.net.URL
import java.util.{Calendar, Locale}

import akka.actor.{Actor, ActorRef, _}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import crawler.model._
import morfologik.stemming.polish.PolishStemmer
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContextExecutor


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Scraper(indexer: ActorRef, keyWord: String) extends Actor {

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val log: LoggingAdapter = Logging(context.system, this)

  val urlValidator = new UrlValidator()

  val bayesClassifier = new KeywordBayesClassifier()
  val stemmer = new Stemmer(new PolishStemmer, keyWord)
  val wordnetClient = new WordnetClient(log)

  def receive: Receive = {
    case Scrap(url: URL) =>
      log.debug(s"scraping $url")
      val content: Option[Content] = parse(url)
      sender() ! ScrapFinished(url)
      indexer ! Index(url, content)
  }

  def parse(url: URL): Option[Content] = { //todo: refaktor do nowego modelu danych
    val link: String = url.toString

    val response: Connection.Response = Jsoup.connect(link).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
      .execute()
    val contentType: String = response.contentType
    if (contentType.startsWith("text/html")) {
      val doc: Document = response.parse()

      val listOfPosts: List[String] = doc.getElementsByClass("postbody").asScala
        .map(post => post.text())
        .toList
      listOfPosts.map(post => post).foreach(post => log.info(s"Found post: $post"))

      //TODO append date


      //TODO OP
//      val pageClass: mutable.Seq[Element] = doc.getElementsByClass("nav").asScala
//      val originalPoster: String = if(pageClass.nonEmpty && pageClass.head.text().split(" ")(1) != "1") {
//        listOfPosts = listOfPosts.tail
//        listOfPosts.head
//      }
//      else
//        ""

      val title: String = doc.getElementsByTag("title").asScala
        .map(e => e.text())
        .head

      val links: List[URL] = doc.getElementsByTag("a").asScala
        .map(u => {
          if (u.attr("href").startsWith("."))
            url.getHost + u.attr("href").substring(1)
          else
            u.attr("href")
        })
        .map(u => {
          if (!u.startsWith("http"))
            "http://" + u
          else
            u
        })
        .filter(u => urlValidator.isValid(u))
        .map(link => new URL(link))
        .toList

        val filteredListOfPosts = listOfPosts
//            .filter(_.contains(keyWord))
//        filteredListOfPosts.foreach(p => log.info(s"Found valid info: $p"))

        val listOfComments: List[Comment] = filteredListOfPosts
            .map(post => post.toLowerCase(new Locale("pl")).replaceAll("[\\.\\;\\?]+", ""))
            .filter(post => stemmer.evaluateKeyWordPredicate(post))
  //        .filter(post => classifier.evaluateKeyWordPredicate(post))
            .map(post => Comment(post, wordnetClient.evaluateEmotions(post.split("\\s").toList), Calendar.getInstance().toInstant))

      return Some(Content(title, List(keyWord), Some(Data(Comment("TODO", Markedness.NEUTRAL, Calendar.getInstance().toInstant), listOfComments)), links))
    } else {
      //jezeli nie html tylko jakis obrazek to zwracamy None
      return None
    }
  }
}
