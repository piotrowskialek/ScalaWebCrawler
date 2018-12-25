package crawler.actors

import java.net.URL
import java.util.Calendar

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import crawler.model.Markedness.enum2String
import crawler.model._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class DbRepositoryTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val keyword: String = "rysy"
  val supervisor: ActorRef = system actorOf Props(new Supervisor(system))
  val dbRepository: ActorRef = system actorOf Props(new DbRepository())

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "DbRepository actor" must {
    "save content when recieved so" in {
      val url: URL = new URL("http://www.onet.pl")
      val persistData: Persist = Persist(url, List(keyword), Comment("TODO", Markedness.NEUTRAL, Calendar.getInstance().toInstant),
        List(Comment("TODO", Markedness.NEUTRAL, Calendar.getInstance().toInstant)))
      dbRepository ! persistData
      expectMsg(PersistFinished(url))
    }

  }

}
