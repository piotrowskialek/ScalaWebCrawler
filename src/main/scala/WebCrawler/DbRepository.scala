package WebCrawler

import java.net.URL

import akka.actor.Actor
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoCollection, MongoDB}


/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  val mongoClient: MongoClient = MongoClient("localhost", 27017)
  val db: MongoDB = mongoClient("web_crawler")
  val collection: MongoCollection = db("siteInfo")

  def receive: Receive = {
    case Persist(url: URL, info) =>

      println(s"Persist: $url")

      val findQuery = url.toString.replace(".",";") $exists true
      val result: MongoCursor = collection.find(findQuery)
      if(result.size == 0){

        val insertDocument = MongoDBObject(url.toString.replace(".",";") -> List[String](info))
        collection.insert(insertDocument)
        println(s"Saving: $url and $info")
        sender() ! PersistingFinished(url)

      } else {

        val parsedInfos: AnyRef = result.one().get(url.toString.replace(".",";"))
        var infos: List[Any] = parsedInfos match {
          case list: BasicDBList => list.toList
          case _ => throw new ClassCastException
        }
        infos = info :: infos
        val updateDocument = MongoDBObject(url.toString.replace(".",";") -> infos)
        collection.update(findQuery, updateDocument)
        println(s"Saving: $url and $info")
        sender() ! PersistingFinished(url)

      }

  }
}
