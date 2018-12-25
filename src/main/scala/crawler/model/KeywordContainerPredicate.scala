package crawler.model

trait KeywordContainerPredicate {
  def checkSenseAndGetAssociatedKeywords(post: String): (Boolean, List[String])
}
