package crawler.actors

import java.net.URL

import akka.actor.{Actor, ActorRef, _}
import akka.event.Logging
import crawler.model._


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, dbRepository: ActorRef) extends Actor {

  var indexedPages = Map.empty[URL, Option[Content]]

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Index(url, content: Option[Content]) =>
      log.debug(s"indexing page $url")
      indexedPages += (url -> content)
      content.flatMap(_.data).filter(_.listOfComments.nonEmpty).foreach(data => {
        dbRepository ! Persist(url, data.originalPost, data.listOfComments)
      })
      val urls = content.map(_.urls).getOrElse(List())
      supervisor ! IndexFinished(url, urls)
      sender() ! IndexFinished(url, urls)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}