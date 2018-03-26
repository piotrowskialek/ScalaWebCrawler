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

//  val stemmer = new PolishStemmer
//
//  val in = "Nie zabrakło oczywiście wpadek. " +
//    "Największym zaskoczeniem okazał się dla nas strój Katarzyny Zielińskiej, " +
//    "której ewidentnie o coś chodziło, " +
//    "ale wciąż nie wiemy o co."
//
//  val splitted = in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+")
//
//  for ( t <- in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+") ) {
//    println("> '" + t + "'")
//      stemmer.lookup(t)
//        .forEach(wd => {
//            print("  - " +
//              (if (wd.getStem == null) "<null>"
//              else wd.getStem) + ", " + wd.getTag)
//          }
//        )
//    println()
//  }


  implicit val system: ActorSystem = ActorSystem()

  val keyWord: String = args(0)
  val supervisor: ActorRef = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))

  Await.result(system.whenTerminated, 5 hours)

  supervisor ! PoisonPill
  system.terminate

}
