package crawler.model

trait KeywordContainerPredicate {
  def evaluateKeyWordPredicate(post: String): Boolean
}
