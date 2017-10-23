package WebCrawler

import java.net.URL

import akka.actor.{Actor, ActorRef}

/**
  * Created by apiotrowski on 14.10.2017.
  */
class Indexer(supervisor: ActorRef, repository: ActorRef) extends Actor {
  var repository2 = Map.empty[URL, Content]

  def receive: Receive = {
    case Index(url, content) =>
      println(s"saving page $url with $content")
      repository2 += (url -> content)
      supervisor ! IndexFinished(url, content.urls)
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = { //zastanowic sie czy moze trzymac wsio w ramie i po stopie persist
    super.postStop()
    repository2.foreach(println)
    println(repository2.size)
  }
}