package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef, Props, _}
import akka.pattern.ask
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Scraper(indexer: ActorRef, keyWord: String) extends Actor {

  val urlValidator = new UrlValidator()
  val stemmer: ActorRef = context actorOf Props(new Stemmer)

  def receive: Receive = {
    case Scrap(url: URL) =>
      println(s"scraping $url")
      val content: Content = parse(url)
      sender() ! ScrapFinished(url)
      indexer ! Index(url, content)
  }

  def parse(url: URL): Content = {
    val link: String = url.toString
    val response: Connection.Response = Jsoup.connect(link).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()

    val contentType: String = response.contentType
    if (contentType.startsWith("text/html")) {

      val doc: Document = response.parse()

      var listOfInfos: List[String] = doc.getElementsByClass("postbody").asScala
        .map(e => e.text())
        .filter(s => s.toLowerCase.contains(keyWord))
        .toList


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

      val d: Future[Any] = stemmer ? Stem("")

      listOfInfos = listOfInfos.map(s => {
        Await.result((stemmer ? Stem(s)).mapTo[StemFinished], Duration(1000, "milis")).stem
      })

      return Content(title, listOfInfos, links)
    } else {
      return Content(link, List(), List()) //jezeli nie html tylko jakis obrazek to pusty kontent
    }
  }
}
