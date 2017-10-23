package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorSystem, Props, _}

import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
class Supervisor(system: ActorSystem) extends Actor {

  val dbRepository: ActorRef = context actorOf Props(new DbRepository())
  val indexer: ActorRef = context actorOf Props(new Indexer(self, dbRepository))

  val maxPages = 100
  val maxRetries = 2

  var numVisited = 0
  var toScrap: Set[URL] = Set.empty
  var numToScrap: Int = toScrap.size
  var scrapCounts = Map.empty[URL, Int]
  var hostActorRepository = Map.empty[String, ActorRef]

  def receive: Receive = {
    case Start(url) =>
      println(s"starting $url")
      scrap(url)
    case ScrapFinished(url) =>
      println(s"scraping finished $url")
    case IndexFinished(url, urls) =>
      if (numVisited < maxPages)
        urls.toSet.filter(l => !scrapCounts.contains(l)).foreach(scrap)
      checkAndShutdown(url)
    case ScrapFailure(url, reason) =>
      val retries: Int = scrapCounts(url)
      println(s"scraping failed $url, $retries, reason = $reason")
      if (retries < maxRetries) {
        countVisits(url)
        hostActorRepository(url.getHost) ! Scrap(url)
      } else
        checkAndShutdown(url)
  }

  def checkAndShutdown(url: URL): Unit = {
    toScrap -= url
    // if nothing to visit
    if (toScrap.isEmpty) {
      self ! PoisonPill
      system.terminate()
    }
  }

  def scrap(url: URL): Unit = {
    val host = url.getHost
    println(s"host = $host")
    if (!host.isEmpty) {
      val siteCrawler = hostActorRepository.getOrElse(host, {
        val newSiteCrawler = system actorOf Props(new SiteCrawler(self, indexer))
        hostActorRepository += (host -> newSiteCrawler)
        newSiteCrawler
      })

      numVisited += 1
      numToScrap += 1
      toScrap += url

      countVisits(url)
      siteCrawler ! Scrap(url)
    }
  }

  def countVisits(url: URL): Unit = scrapCounts += (url -> (scrapCounts.getOrElse(url, 0) + 1))
}