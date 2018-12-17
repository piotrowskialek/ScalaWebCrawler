package crawler.model

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import crawler.actors.Supervisor
import org.scalatest.FlatSpec

class WordnetClientTest extends FlatSpec{

  implicit val system: ActorSystem = ActorSystem()
  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, "")))
  val log: LoggingAdapter = Logging(system, supervisor) //has to create for a logger
  val wordnetClient = new WordnetClient(log)


  "Ten samochód jest kiepski" should "be NEGATIVE" in {
    assert(wordnetClient
      .evaluateEmotions("Ten samochód jest kiepski".split(" ").toList) == Markedness.NEGATIVE)
  }

  "Ten samochód jest świetny" should "be POSITIVE" in {
    assert(wordnetClient
      .evaluateEmotions("Ten samochód jest świetny".split(" ").toList) == Markedness.POSITIVE)
  }

  "To jest jabłko" should "be NEUTRAL" in {
    assert(wordnetClient
      .evaluateEmotions("To jest jabłko".split(" ").toList) == Markedness.NEUTRAL)
  }

  "Może się wydawać że trasa na Rysy jest trudna" should "be NEGATIVE" in {
    assert(wordnetClient
      .evaluateEmotions("Może się wydawać że trasa na Rysy jest trudna".split("\\s").toList) == Markedness.NEGATIVE)
  }

  system.terminate

}
