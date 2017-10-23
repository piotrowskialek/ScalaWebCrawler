package WebCrawler

import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
object Main extends App {

  val system = ActorSystem()
  val keyWord: String = args(0)
  val supervisor = system.actorOf(Props(new Supervisor(system, keyWord)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/viewforum.php?f=4"))

  Await.result(system.whenTerminated, 20 minutes)

  supervisor ! PoisonPill
  system.terminate

}
