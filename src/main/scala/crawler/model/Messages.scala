package crawler.model

import java.net.URL

/**
  * Created by apiotrowski on 14.10.2017.
  */
case class Start(url: URL)
case class Scrap(url: URL)
case class Index(url: URL, content: Content)
case class ProcessNextUrl()

case class Content(title: Option[String], keywords: List[String], originalPost: Comment, listOfComments: List[Comment], urls: List[URL])

case class ScrapFinished(url: URL)
case class IndexFinished(url: URL, urls: List[URL])
case class ScrapFailure(url: URL, reason: Throwable)

case class Persist(url: URL, keywords: List[String], originalPost: Comment, listOfComments: List[Comment])

case class PersistFinished(url: URL)
case class PersistFailed(url: URL, reason: Throwable)

case class Stem(word: String)
case class StemFinished(word: String, stem: String)

case class SendWordnetReq(word: String)

case class Comment(post: String, emotion: Markedness.Value)