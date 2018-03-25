package WebCrawler.actors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.JsonParser

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class WordnetClient(implicit val system: ActorSystem, implicit val materializer: ActorMaterializer, implicit val executionContext: ExecutionContextExecutor) {

  val WORDNET_URL = "http://ws.clarin-pl.eu/lexrest/lex/"
  val DATA_PATTERN = "{\"task\":\"all\",\"lexeme\":\"REPLACE\",\"tool\":\"all\"}"

  def parse(word: String): Boolean = {
    val future: Future[HttpResponse] = sendWordnetReq(word)
      future.onComplete {
        case Success(res) => Unmarshal(res.entity).to[String]
            .onComplete(f => {
              val result = JsonParser(f.getOrElse[String]("{}")).asJsObject
              println(result)
            })
          println(s"Consuming wordnet service with $word done with success")
        case Failure(_) => println("WORDNET CLIENT ERROR")
      }
    true
  }


  def sendWordnetReq(word: String): Future[HttpResponse] = {
    val data: String = DATA_PATTERN.replace("REPLACE", word)
    Http()
      .singleRequest(HttpRequest(
        entity = HttpEntity(data).withContentType(ContentTypes.`application/json`),
        uri = WORDNET_URL,
        method = HttpMethods.POST))

  }

  def parseResult(): Unit = {}

}
