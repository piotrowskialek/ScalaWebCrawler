package WebCrawler.model

trait KeywordContainerPredicate {
  def evaluateKeyWordPredicate(post: String): Boolean
}
