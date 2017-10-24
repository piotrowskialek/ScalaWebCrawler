package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef}
import org.apache.commons.validator.routines.UrlValidator
import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._

/**
  * Created by apiotrowski on 14.10.2017.
  */
class Scraper(indexer: ActorRef, keyWord: String) extends Actor {
  val urlValidator = new UrlValidator()

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

      val listOfInfos: List[String] = doc.getElementsByClass("postbody").asScala
        .map(e => e.text())
        .filter(s => s.toLowerCase.contains(keyWord))
        .toList

      val title: String = doc.getElementsByTag("title").asScala
        .map(e => e.text())
        .head
      val links: List[URL] = doc.getElementsByTag("a").asScala
        .map(e => e.attr("href"))
        .filter(s => urlValidator.isValid(s))
        .map(link => new URL(link))
        .toList
      return Content(title, listOfInfos, links)
    } else {
      //if not a html document for example an image
      return Content(link, List(), List())
    }
  }
}
