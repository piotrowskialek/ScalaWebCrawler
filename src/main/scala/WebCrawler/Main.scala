package WebCrawler

import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}


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

//  val stemmer = new Stemmer(new PolishStemmer(), "rysy")
//  println(stemmer.keywordPredicate("Pogoda na rysy jest kiepska"))
//  println(stemmer.keywordPredicate("xd Pogoda na rysy jest kiepska xd"))
//  println(stemmer.keywordPredicate("xD Pogoda na rysach jest słaba xD"))
//  println(stemmer.keywordPredicate("Pogoda na rysach jest kiepska"))
//  println(stemmer.keywordPredicate("na telefonie mam rysy xD"))




  implicit val system = ActorSystem()//zmienic z implicit
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))
  responseFuture
    .onComplete {
      case Success(res) => println(res)
      case Failure(_)   => sys.error("something wrong")
    }


  val keyWord: String = args(0)
  val supervisor = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))

  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))

  Await.result(system.whenTerminated, 5 hours)

  supervisor ! PoisonPill
  system.terminate

}
