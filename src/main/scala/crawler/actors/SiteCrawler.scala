package crawler.actors

import java.net.URL

import akka.actor.{Actor, Props, _}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import crawler.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

/**
  * Created by apiotrowski on 14.10.2017.
  */
class SiteCrawler(supervisor: ActorRef, indexer: ActorRef) extends Actor {

  val scraper: ActorRef = context actorOf Props(new Scraper(indexer))

  val log = Logging(context.system, this)

  implicit val timeout: Timeout = Timeout(240 seconds)
  val tick: Cancellable = context.system.scheduler.schedule(0 millis, 1000 + Random.nextInt(2000) millis, self, ProcessNextUrl())
  //udawanie uzytkownika, losowe momenty
  var toProcess: List[URL] = List.empty[URL]

  def receive: Receive = {
    case Scrap(url) =>
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