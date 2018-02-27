package WebCrawler

import javax.ws.rs.client._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.JsonParser

import scala.concurrent.Future
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

  val task = "{\"task\":\"all\",\"lexeme\":\"jechać\",\"tool\":\"all\"}"
  val url = "http://ws.clarin-pl.eu/lexrest/lex/"



  implicit val system = ActorSystem() //zmienic z implicit
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val responseFuture: Future[HttpResponse] = Http().singleRequest(
    HttpRequest(entity = HttpEntity(task).withContentType(ContentTypes.`application/json`), uri = url, method = HttpMethods.POST))


  responseFuture
    .onComplete {
      case Success(res) => println(Unmarshal(res.entity).to[String]
        .onComplete(f=> println(JsonParser(f.get).asJsObject)))
      case Failure(_)   => sys.error("something wrong")
    }

  //  val keyWord: String = args(0)
  //  val supervisor = system.actorOf(Props(new Supervisor(system, keyWord.toLowerCase)))
  //
  //  supervisor ! Start(new URL("http://forum.turystyka-gorska.pl/index.php"))
  //
  //  Await.result(system.whenTerminated, 5 hours)
  //
  //  supervisor ! PoisonPill
  system.terminate





  val client = ClientBuilder.newClient
  val req = client
    .target(url)
    .request
  val res = req.post(Entity.entity(task, javax.ws.rs.core.MediaType.APPLICATION_JSON)).readEntity(classOf[String])

//  println(JSON.parseFull(res))



}
