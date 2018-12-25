package crawler

import java.net.URL

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import crawler.actors.Supervisor
import crawler.model.Start

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * Created by apiotrowski on 14.10.2017.
  */

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()

  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))

  Await.result(system.whenTerminated, 5 hours)

  supervisor ! PoisonPill
  system.terminate

}
