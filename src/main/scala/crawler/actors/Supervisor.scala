package crawler.actors

import java.net.URL

import akka.actor.{Actor, ActorSystem, Props, _}
import akka.event.Logging
import crawler.model._

import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
class Supervisor(system: ActorSystem) extends Actor {

  val dbRepository: ActorRef = context actorOf Props(new DbRepository())
  val indexer: ActorRef = context actorOf Props(new Indexer(self, dbRepository))

  val log = Logging(context.system, this)

  val maxPages: Int = 500000
  val maxRetries: Int = 5

  var numVisited: Int = 0
  var indexedUrls: Set[URL] = Set.empty
  var scrapCounts: Map[URL, Int] = Map.empty
  var hostActorRepository: Map[String, ActorRef] = Map.empty
  val listOfForbiddenHosts: List[String] = List[String]("google","facebook","twitter") //zastanowic sie czy tak ladnie

  def receive: Receive = {
    case Start(url) =>
      log.info(s"starting $url")
      scrap(url)
    case ScrapFinished(url) =>
      log.info(s"scraping finished $url")
    case IndexFinished(url, urls) =>
      if (numVisited < maxPages)
        urls.toSet
          .filter(l => !scrapCounts.contains(l))
          .filter(l => !listOfForbiddenHosts.exists(l.getHost.contains(_)))
          .filter(_.getHost.contains("forum.turystyka-gorska.pl"))
          .filter(!_.toURI.toString.contains("memberlist"))
          .filter(!_.toURI.toString.contains("ucp.php"))
          .filter(!_.toURI.toString.contains("posting.php"))
          .foreach(scrap)

      checkAndShutdown(url)
    case ScrapFailure(url, reason) =>
      val retries: Int = scrapCounts(url)
      log.error(s"scraping failed $url, $retries, reason = $reason")
      if (retries < maxRetries) {
        countVisits(url)
        hostActorRepository(url.getHost) ! Scrap(url)
      } else
        checkAndShutdown(url)
  }

  def checkAndShutdown(url: URL): Unit = {
    indexedUrls -= url
    if (indexedUrls.isEmpty) {
      self ! PoisonPill
      system.terminate()
    }
  }

  def scrap(url: URL): Unit = {
    val host = url.getHost
    log.debug(s"host = $host")
    if (!host.isEmpty) {
      val siteCrawler = hostActorRepository.getOrElse(host, {
        val newSiteCrawler = system actorOf Props(new SiteCrawler(self, indexer))
        hostActorRepository += (host -> newSiteCrawler)
        newSiteCrawler
      })

      numVisited += 1
      indexedUrls += url

      countVisits(url)
      siteCrawler ! Scrap(url)
    }
  }

  def countVisits(url: URL): Unit = scrapCounts += (url -> (scrapCounts.getOrElse(url, 0) + 1))
}