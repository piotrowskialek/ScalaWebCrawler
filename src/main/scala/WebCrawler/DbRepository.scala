package WebCrawler

import java.net.URL

import akka.actor.Actor
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoCollection, MongoDB}


/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  val mongoClient: MongoClient = MongoClient("localhost", 27017)
  val db: MongoDB = mongoClient("web_crawler")
  val collection: MongoCollection = db("site_info")

  def receive: Receive = {
    case Persist(url: URL, info) =>

      val insertDocument = MongoDBObject("url" -> url.toString.replace(".", ";"))
      insertDocument.put("infos", List[String](info))
      collection.insert(insertDocument)
      println(s"Saving: $url and $info")
      sender() ! PersistFinished(url)
  }
}
