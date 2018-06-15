package WebCrawler.actors

import java.net.URL
import java.util.Locale

import WebCrawler.model.{Markedness, Stemmer, WordnetClient}
import WebCrawler.{Content, Index, Scrap, ScrapFinished}
import akka.actor.{Actor, ActorRef, _}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import morfologik.stemming.polish.PolishStemmer
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.nodes.{Document, Element}
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._
import scala.collection.mutable
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

  val stemmer = new Stemmer(new PolishStemmer, keyWord)
  val wordnetClient = new WordnetClient(log)

  def receive: Receive = {
    case Scrap(url: URL) =>
      log.debug(s"scraping $url")
      val content: Content = parse(url)
      sender() ! ScrapFinished(url)
      indexer ! Index(url, content)
  }

  def parse(url: URL): Content = {
    val link: String = url.toString

    val response: Connection.Response = Jsoup.connect(link).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
      .execute()

    val contentType: String = response.contentType
    if (contentType.startsWith("text/html")) {

      val doc: Document = response.parse()

      var listOfPosts: List[String] = doc.getElementsByClass("postbody").asScala
        .map(post => post.text())
        .toList

      val pageClass: mutable.Seq[Element] = doc.getElementsByClass("nav").asScala
      val originalPoster: String = if(pageClass.nonEmpty && pageClass.head.text().split(" ")(1) != "1") {
        listOfPosts = listOfPosts.tail
        listOfPosts.head
      }
      else
        ""

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

      listOfPosts
        .filter(_.contains(keyWord))
        .foreach(p => log.info(s"found info $p"))

      val listOfPostAndEmotions: List[(String, Markedness.Value)] = listOfPosts
        .map(post => post.toLowerCase(new Locale("pl")).replaceAll("[\\.\\;\\?]+", ""))
        .filter(post => stemmer.evaluateKeyWordPredicate(post))
        .map(post => (post, wordnetClient.evaluateEmotions(post.split(" ").toList)))
        //lista postow -> lista

      //lista par (post usera zawierajacych informacje -> nacechowanie)
      //sprawdzanie regul
      //lista zdan ze slowem kluczowym

      return Content(title, listOfPostAndEmotions, links)
    } else {
      return Content(link, List(), List()) //jezeli nie html tylko jakis obrazek to pusty kontent
    }
  }
}
