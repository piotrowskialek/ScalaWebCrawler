package WebCrawler

import java.net.URL

import akka.actor.{Actor, Props, _}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
class SiteCrawler(supervisor: ActorRef, indexer: ActorRef, keyWord: String) extends Actor {

  val scraper: ActorRef = context actorOf Props(new Scraper(indexer, keyWord))

  val log = Logging(context.system, this)

  implicit val timeout: Timeout = Timeout(3 seconds)
  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 millis, self, ProcessNextUrl())
  var toProcess: List[URL] = List.empty[URL]

  def receive: Receive = {
    case Scrap(url) =>
      // wait some time, so we will not spam a website
      log.debug(s"waiting... $url")
      toProcess = url :: toProcess
    case ProcessNextUrl() =>
      toProcess match {
        case Nil =>
        case url :: list =>
          log.debug(s"site scraping... $url")
          toProcess = list
          (scraper ? Scrap(url)).mapTo[ScrapFinished]
            .recoverWith {
              case e => Future {
                ScrapFailure(url, e)
              }
            }
            .pipeTo(supervisor)
      }
  }
}