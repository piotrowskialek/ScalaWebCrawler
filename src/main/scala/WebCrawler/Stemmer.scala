package WebCrawler

import akka.actor.Actor
import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._

class Stemmer extends Actor {

  val stemmer = new PolishStemmer

  def receive: Receive = {
    case Stem(word: String) =>
      sender() ! StemFinished(word, stem(word))
  }

  def stem(word: String): String = stemmer.lookup(word).asScala.map(wd => wd.getStem.toString).reduce(_ + "/" + _)

}
