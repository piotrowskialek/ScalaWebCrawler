package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef, _}


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {

  var indexedPages = Map.empty[URL, Content]

  def receive: Receive = {
    case Index(url, content) =>
      println(s"indexing page $url")
      indexedPages += (url -> content)
      for (info <- content.attributes)
        repository ! Persist(url, info)
      //        Await.result((repository ? Persist(url, info)).mapTo[PersistFinished], timeout.duration)

      supervisor ! IndexFinished(url, content.urls)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = { //zastanowic sie czy moze trzymac wsio w ramie i po stopie persist
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}