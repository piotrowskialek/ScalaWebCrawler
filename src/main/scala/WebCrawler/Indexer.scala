package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef, _}
import akka.event.Logging


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {

  var indexedPages = Map.empty[URL, Content]

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Index(url, content) =>
      log.debug(s"indexing page $url")
      indexedPages += (url -> content)
      for (info <- content.listOfSentences)
        repository ! Persist(url, info)
      //        Await.result((repository ? Persist(url, info)).mapTo[PersistFinished], timeout.duration)

      supervisor ! IndexFinished(url, content.urls)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}