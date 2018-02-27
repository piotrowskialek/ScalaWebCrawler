package WebCrawler

import akka.actor.Actor

class WordnetService extends Actor {


  override def receive: Receive = {
    case SendWordnetReq(word) =>
  }
}
