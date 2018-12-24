package crawler.actors

import java.net.URL

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import crawler.model.{Index, IndexFinished}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class IndexerTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val keyword: String = "rysy"
  val supervisor: ActorRef = system actorOf Props(new Supervisor(system, keyword.toLowerCase))
  val dbRepository: ActorRef = system actorOf Props(new DbRepository())
  val indexer: ActorRef = system actorOf Props(new Indexer(self, dbRepository))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An indexer actor" must {
    "send back messages with empty urls when recieved no content" in {

      val url: URL = new URL("http://www.onet.pl")
      indexer ! Index(url, None)
      expectMsg(IndexFinished(url, List()))
    }

  }

}
