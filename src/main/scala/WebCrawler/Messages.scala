package WebCrawler

import java.net.URL

/**
  * Created by apiotrowski on 14.10.2017.
  */
case class Start(url: URL)
case class Scrap(url: URL)
case class Index(url: URL, content: Content)
case class Content(title: String, attributes: List[String], urls: List[URL])
case class ScrapFinished(url: URL)
case class IndexFinished(url: URL, urls: List[URL])
case class ScrapFailure(url: URL, reason: Throwable)
case class Persist(url: URL, listOfInfos: String)
case class PersistFinished(url: URL)
case class PersistFailed(url: URL, reason: Throwable)
case class Stem(word: String)
case class StemFinished(word: String, stem: String)