package crawler.actors

import java.net.URL

import akka.actor.Actor
import akka.event.Logging
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoCollection, MongoDB}
import crawler.model._


/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  import DbRepository.collection

  val log = Logging(context.system, this)

  def receive: Receive = {
    case Persist(url: URL, keywords: List[String], originalPost: Comment, listOfComments: List[Comment]) =>
      val insertDocument = MongoDBObject("url" -> url.toString.replace(".", ";")) //mongo krzyczalo jak byly kropki
      insertDocument.put("keywords", keywords)
      insertDocument.put("original-post", originalPost)
      insertDocument.put("list-of-comments", listOfComments)
      collection.insert(insertDocument)
      log.debug(s"Saved OP: $originalPost with ${listOfComments.size} comments")
  }
}

object DbRepository {

  val mongoClient: MongoClient = MongoClient("localhost", 27017)
  val db: MongoDB = mongoClient("web_crawler")
  val collection: MongoCollection = db("site_data")
}