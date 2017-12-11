package WebCrawler

import akka.actor.Actor

class Stemmer extends Actor {

  def receive: Receive = {
    case Stem(word: String) =>
      sender() ! StemFinished(word, stem(word))
  }

  def stem(word: String): String = {
    word
  }

}
