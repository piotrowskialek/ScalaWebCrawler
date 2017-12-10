package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef}


/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {

  var indexedPages = Map.empty[URL, Content]
//  val repository2: ActorRef = context actorOf Props(new DbRepository)


  def receive: Receive = {
    case Index(url, content) =>
      println(s"indexing page $url")
      indexedPages += (url -> content)
      for(info <- content.attributes)
        supervisor ! IndexFinished(url, content.urls)
    //        (repository2 ? Persist(url, info)).mapTo[PersistFinished]
    //          .recoverWith { case e => Future {
    //            PersistFailed(url, e)
    //          }
    //          }

    //      repository ! Persist(url, info)

  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = { //zastanowic sie czy moze trzymac wsio w ramie i po stopie persist
    super.postStop()
    indexedPages.foreach(println)
    println(indexedPages.size)
  }
}