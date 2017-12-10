package WebCrawler

import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}
import morfologik.stemming.polish.PolishStemmer

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */
object Main extends App {

  val stemm = new PolishStemmer
  val xD = stemm.lookup("białe")
  val res = stemm.lookup("białe").get(0).getStem
  val res2 = stemm.lookup("białe").get(0).getTag
  val res3 = stemm.lookup("białe").get(0).getWord

  val system = ActorSystem()
  val keyWord: String = args(0)
  val supervisor = system.actorOf(Props(new Supervisor(system, keyWord)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/viewforum.php?f=4"))

  Await.result(system.whenTerminated, 20 minutes)

  supervisor ! PoisonPill
  system.terminate

}
