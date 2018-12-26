package crawler.actors

import java.net.URL

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import crawler.model.{Scrap, ScrapFinished}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class ScraperTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val keyword: String = "rysy"
  val supervisor: ActorRef = system actorOf Props(new Supervisor(system))
  val dbRepository: ActorRef = system actorOf Props(new DbRepository())
  val indexer: ActorRef = system actorOf Props(new Indexer(self, dbRepository))
  val scraper: ActorRef = system actorOf Props(new Scraper(indexer))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A scraper actor" must {
    "scrap" in {
      implicit val timeout: FiniteDuration = 5.seconds
      val url: URL = new URL("http://forum.turystyka-gorska.pl/viewtopic.php?f=1&t=6970")
      scraper ! Scrap(url)
      expectMsg(ScrapFinished(url))
    }

  }

}
