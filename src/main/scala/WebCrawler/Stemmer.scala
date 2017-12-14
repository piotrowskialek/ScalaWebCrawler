package WebCrawler

import akka.actor.Actor
import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConversions._

class Stemmer extends Actor {

  val stemmer = new PolishStemmer

  def receive: Receive = {
    case Stem(word: String) =>
      sender() ! StemFinished(word, stem(word))
  }

  def stem(word: String): String = stemmer.lookup(word).map(wd => wd.getStem.toString).reduce(_ + "/" + _)

}
