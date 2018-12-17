package crawler.actors

import java.net.URL

import akka.actor.{Actor, ActorRef, _}
import akka.event.Logging
import crawler.model._


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {

  var indexedPages = Map.empty[URL, Content]

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Index(url, content: Content) =>
      log.debug(s"indexing page $url")
      indexedPages += (url -> content)
      if (content.title.isDefined && content.listOfComments.nonEmpty) {
        repository ! Persist(url, content.keywords, content.originalPost, content.listOfComments)
      }
      supervisor ! IndexFinished(url, content.urls)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}