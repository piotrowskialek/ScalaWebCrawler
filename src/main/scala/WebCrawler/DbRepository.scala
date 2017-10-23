package WebCrawler

import java.net.URL

import akka.actor.Actor

/**
  * Created by apiotrowski on 23.10.2017.
  */
class DbRepository() extends Actor {

  //tutaj trzymane polaczenie z baza


  def receive: Receive = {
    case Persist(url: URL, content: Content) =>
      println(s"Persist: $url")
      //save

  }
}
