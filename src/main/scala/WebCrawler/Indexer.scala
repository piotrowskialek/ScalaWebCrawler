package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef}


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {
  var indexedPages = Map.empty[URL, Content]

  def receive: Receive = {
    case Index(url, content) =>
      println(s"saving page $url with $content")
      indexedPages += (url -> content)
      for(info <- content.listOfInfos)
        (repository ! Persist(url, info))
//          .mapTo[PersistingFinished].recoverWith({
//          case e => Future {println(s"Persisting: $url failed!!!"); PersistingFailed(url)}})

      supervisor ! IndexFinished(url, content.urls)

  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = { //zastanowic sie czy moze trzymac wsio w ramie i po stopie persist
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}