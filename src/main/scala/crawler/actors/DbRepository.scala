package crawler.actors

import java.net.URL

import akka.actor.Actor
import akka.event.Logging
import crawler.model._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  import DbRepository.collection

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Persist(url: URL, originalPost: Comment, listOfComments: List[Comment]) =>

      val insertData: InsertData = InsertData(url.toString.replace(".", ";"),
          Data(originalPost, listOfComments))

      collection.insertOne(insertData).subscribe(new Observer[Completed] {
        override def onNext(result: Completed): Unit = None
        override def onError(e: Throwable): Unit = log.error(s"Failed saving OP: $originalPost Exception: $e")
        override def onComplete(): Unit = log.info(s"Saved OP: $originalPost with ${listOfComments.size} comments")
      })
      sender() ! PersistFinished(url)
  }
}

object DbRepository {
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[InsertData],
    classOf[Data], classOf[Comment]), DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:27017/web_crawler")
  val collection: MongoCollection[InsertData] = mongoClient
    .getDatabase("web_crawler")
    .withCodecRegistry(codecRegistry)
    .getCollection("site_data")
}