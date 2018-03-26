package WebCrawler.model

import WebCrawler.actors.Supervisor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.scalatest.FlatSpec

class WordnetClientTest extends FlatSpec{

  implicit val system: ActorSystem = ActorSystem()
  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, "")))
  val log: LoggingAdapter = Logging(system, supervisor) //has to create for logger
  val wordnetClient = new WordnetClient(log)


  "Ten samochód jest kiepski" should "be NEGATIVE" in {
    assert(wordnetClient
      .valuateEmotions("Ten samochód jest kiepski".split(" ").toList) == Markedness.NEGATIVE)
  }

  "Ten samochód jest świetny" should "be POSITIVE" in {
    assert(wordnetClient
      .valuateEmotions("Ten samochód jest świetny".split(" ").toList) == Markedness.POSITIVE)
  }

  "To jest jabłko" should "be NEUTRAL" in {
    assert(wordnetClient
      .valuateEmotions("To jest jabłko".split(" ").toList) == Markedness.NEUTRAL)
  }

  "Może się wydawać że trasa na Rysy jest trudna" should "be NEGATIVE" in {
    assert(wordnetClient
      .valuateEmotions("Może się wydawać że trasa na Rysy jest trudna".split(" ").toList) == Markedness.NEGATIVE)
  }

  system.terminate

}
