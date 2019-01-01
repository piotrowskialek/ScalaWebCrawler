package crawler.actors

import java.net.URL

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

    "update correctly OP" in {
      val url: URL = new URL("http://www.forum.turystyka-gorska.pl/viewtopic.php?f=13&t=17586&sid=026204950b8c3db20552bf8f6131acdc")
      val persistData: Persist = Persist(
        url,
        Comment("TODO", Markedness.NEUTRAL, Option("date"), List("")),
        List(Comment("TODO", Markedness.NEUTRAL, Option("date"), List("")))
      )
      dbRepository ! persistData
      expectMsg(PersistFinished(url))
    }

    "save content when recieved so" in {
      val url: URL = new URL("http://www.onet.pl")
      val persistData: Persist = Persist(
        url,
        Comment("TODO", Markedness.NEUTRAL, Option("date"), List("")),
        List(Comment("TODO", Markedness.NEUTRAL, Option("date"), List("")))
      )
      dbRepository ! persistData
      expectMsg(PersistFinished(url))
    }

  }

}
