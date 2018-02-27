package WebCrawler

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.JsonParser

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class WordnetClient() extends Actor {

  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val WORDNET_URL = "http://ws.clarin-pl.eu/lexrest/lex/"
  val DATA_PATTERN = "{\"task\":\"all\",\"lexeme\":\"REPLACE\",\"tool\":\"all\"}"

  def receive: Receive = {
    case SendWordnetReq(word) => val future: Future[HttpResponse] = sendWordnetReq(word)
      future.onComplete {
        case Success(res) => Unmarshal(res.entity).to[String]
            .onComplete(f => {
              JsonParser(f.get).asJsObject
            })
        case Failure(_) => sys.error("WORDNET CLIENT ERROR")
      }
  }


  def sendWordnetReq(word: String): Future[HttpResponse] = {
    val data: String = DATA_PATTERN.replace("REPLACE", word)
    Http()
      .singleRequest(HttpRequest(
        entity = HttpEntity(data).withContentType(ContentTypes.`application/json`),
        uri = WORDNET_URL,
        method = HttpMethods.POST))

  }
}
