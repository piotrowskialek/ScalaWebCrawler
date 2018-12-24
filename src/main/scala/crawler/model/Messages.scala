package crawler.model

import java.net.URL
import java.time.Instant

/**
  * Created by apiotrowski on 14.10.2017.
  */
case class Start(url: URL)
case class Scrap(url: URL)
case class Index(url: URL, content: Option[Content])
case class ProcessNextUrl()

case class Content(title: String, keywords: List[String], data: Option[Data], urls: List[URL])
case class Data(originalPost: Comment, listOfComments: List[Comment])
case class Comment(post: String, emotion: String, dateOfPost: Instant)

case class ScrapFinished(url: URL)
case class IndexFinished(url: URL, urls: List[URL])
case class ScrapFailure(url: URL, reason: Throwable)

case class Persist(url: URL, keywords: List[String], originalPost: Comment, listOfComments: List[Comment])
case class PersistFinished(url: URL)
case class PersistFailed(url: URL, reason: Throwable)
case class InsertData(url: String, keywords: List[String], data: Data)

case class Stem(word: String)
case class StemFinished(word: String, stem: String)

case class SendWordnetReq(word: String)

