package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorSystem, Props, _}

import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
class Supervisor(system: ActorSystem, keyWord: String) extends Actor {

  val dbRepository: ActorRef = context actorOf Props(new DbRepository())
  val indexer: ActorRef = context actorOf Props(new Indexer(self, dbRepository))

  val maxPages = 50000
  val maxRetries = 2

  var numVisited = 0
  var indexedUrls: Set[URL] = Set.empty
  var scrapCounts = Map.empty[URL, Int]
  var hostActorRepository = Map.empty[String, ActorRef]
  val listOfForbiddenHosts: List[String] = List[String]("google","facebook","twitter") //zastanowic sie czy tak ladnie

  def receive: Receive = {
    case Start(url) =>
      println(s"starting $url")
      scrap(url)
    case ScrapFinished(url) =>
      println(s"scraping finished $url")
    case IndexFinished(url, urls) =>
      if (numVisited < maxPages)
        urls.toSet
          .filter(l => !scrapCounts.contains(l))
          .filter(l => !listOfForbiddenHosts.exists(l.getHost.contains(_)))
          .filter(l => l.getHost.contains("forum.turystyka-gorska.pl"))////////////////todo
          .foreach(scrap)

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
    indexedUrls -= url
    // if nothing to visit
    if (indexedUrls.isEmpty) {
      self ! PoisonPill
      system.terminate()
    }
  }

  def scrap(url: URL): Unit = {
    val host = url.getHost
    println(s"host = $host")
    if (!host.isEmpty) {
      val siteCrawler = hostActorRepository.getOrElse(host, {
        val newSiteCrawler = system actorOf Props(new SiteCrawler(self, indexer, keyWord))
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