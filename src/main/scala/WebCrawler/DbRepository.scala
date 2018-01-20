package WebCrawler

import java.net.URL

import akka.actor.Actor
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoCollection, MongoCursor, MongoDB}


/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  val mongoClient: MongoClient = MongoClient("localhost", 27017)
  val db: MongoDB = mongoClient("web_crawler")
  val collection: MongoCollection = db("site_data")

  def receive: Receive = {
    case Persist(url: URL, info) =>
      val insertDocument = MongoDBObject("url" -> url.toString.replace(".", ";")) //mongo krzyczalo jak byly kropki
      insertDocument.put("data", info)
      val selectObject = MongoDBObject("data" -> info)
      val select: MongoCursor = collection.find(selectObject)

      if (select.size == 0) {
        collection.insert(insertDocument)
        println(s"Saving: $url and $info")
      }
      else {
        println(s"$info already saved.")
      }

      sender() ! PersistFinished(url)

  }
}
