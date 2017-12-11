package WebCrawler

import java.net.URL
import java.util.Locale

import akka.actor.{ActorSystem, PoisonPill, Props}
import morfologik.stemming.polish.PolishStemmer

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by apiotrowski on 14.10.2017.
  */

object Main extends App {

  val stemmer = new PolishStemmer

  val in = "Nie zabrakło oczywiście wpadek. " +
    "Największym zaskoczeniem okazał się dla nas strój Katarzyny Zielińskiej, " +
    "której ewidentnie o coś chodziło, " +
    "ale wciąż nie wiemy o co."

  val splitted = in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+")

  for ( t <- in.toLowerCase(new Locale("pl")).split("[\\s\\.\\,]+") ) {
    println("> '" + t + "'")
      stemmer.lookup(t)
        .forEach(wd => {
            print("  - " +
              (if (wd.getStem == null) "<null>"
              else wd.getStem) + ", " + wd.getTag)
          }
        )
    println()
  }

  val stemm = new PolishStemmer
  val xD = stemm.lookup("białołęka")
  val res = stemm.lookup("białołęka").get(0).getStem
  val res2 = stemm.lookup("białołęka").get(0).getTag
  val res3 = stemm.lookup("białołęka").get(0).getWord

  val system = ActorSystem()
  val keyWord: String = args(0)
  val supervisor = system.actorOf(Props(new Supervisor(system, keyWord)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/viewforum.php?f=4"))

  Await.result(system.whenTerminated, 20 minutes)

  supervisor ! PoisonPill
  system.terminate

}
