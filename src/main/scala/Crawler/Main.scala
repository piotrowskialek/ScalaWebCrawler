package Crawler

import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
object Main extends App{

  val system = ActorSystem()
  val supervisor = system.actorOf(Props(new Supervisor(system)))

  supervisor ! Start(new URL("https://foat.me"))

  Await.result(system.whenTerminated, 10 minutes)

  supervisor ! PoisonPill
  system.terminate


}
