package crawler.actors

import java.net.URL
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.event.Logging
import crawler.model._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  import DbRepository.collection

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Persist(url: URL, originalPost: Comment, listOfComments: List[Comment]) =>


      val parsedURL: String = url.toString.split("&").filter(!_.contains("start")).reduce(_ + "&" + _)
      //przed zapisem sprawdz czy jest juz doc z postami z tego freda
      //jak jest to doklej fredy i co z postem opa na podstawie czego stwierdzic? jezeli url przyjdzie bez start #esesman
      val insertData: MongoData = MongoData(parsedURL.replace(".", ";"), Data(originalPost, listOfComments))

      val existingCollection: Option[MongoData] = Await.result(collection.find(equal("url", parsedURL)).headOption(), Duration(5, TimeUnit.SECONDS))

      collection.insertOne(insertData).subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = None
        override def onError(e: Throwable): Unit = log.error(s"Failed saving OP: $originalPost Exception: $e")
        override def onComplete(): Unit = log.info(s"Saved OP: $originalPost with ${listOfComments.size} comments")
      })
      sender() ! PersistFinished(url)
  }
}

object DbRepository {
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[MongoData],
    classOf[Data], classOf[Comment]), DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017/web_crawler")
  val collection: MongoCollection[MongoData] = mongoClient
    .getDatabase("web_crawler")
    .withCodecRegistry(codecRegistry)
    .getCollection("site_data")
}