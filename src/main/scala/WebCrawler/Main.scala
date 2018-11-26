package WebCrawler

import java.net.URL

import WebCrawler.actors.Supervisor
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * Created by apiotrowski on 14.10.2017.
  */

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()

  val keyWord: String = Option(args(0)).getOrElse("rysy")
  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))

  Await.result(system.whenTerminated, 5 hours)

  supervisor ! PoisonPill
  system.terminate

}
