package WebCrawler

import java.net.URL

/**
  * Created by apiotrowski on 14.10.2017.
  */
case class Start(url: URL)
case class Scrap(url: URL)
case class Index(url: URL, content: Content)
case class ProcessNextUrl()
case class Content(title: String, listOfSentences: List[String], urls: List[URL])
case class ScrapFinished(url: URL)
case class IndexFinished(url: URL, urls: List[URL])
case class ScrapFailure(url: URL, reason: Throwable)
case class Persist(url: URL, listOfInformations: String)
case class PersistFinished(url: URL)
case class PersistFailed(url: URL, reason: Throwable)
case class Stem(sentences: List[String])
case class StemFinished(results: Map[String, Boolean])
case class SendWordnetReq(word: String)
