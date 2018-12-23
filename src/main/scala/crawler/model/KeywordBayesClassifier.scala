package crawler.model

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._
import scala.io.BufferedSource

class KeywordBayesClassifier(stemmer: PolishStemmer, keyword: String) extends KeywordContainerPredicate {

  import KeywordBayesClassifier.bayesClassifier

  override def evaluateKeyWordPredicate(post: String): Boolean = {
    if (!hasKeywordInAnyForm(post))
      return false
    bayesClassifier.classify(List(post).asJava).getCategory
  }

  def hasKeywordInAnyForm(post: String): Boolean = {
    var keywordStem: String = ""
    val lookup = stemmer.lookup(keyword)
    if (lookup.isEmpty)
      keywordStem = keyword //case kiedy keyword sie nie stemuje, wtedy uznaje ze stem = keyword
    keywordStem = lookup.get(0).getStem.toString

    val listOfPostStems: Seq[String] = post.split(" ")
      .map(word => stemmer.lookup(word).asScala.map(_.getStem).foldLeft("")(_ + "/" + _))

    val stringOfPostStems = listOfPostStems.foldLeft("")(_ + "/" + _)
    if (stringOfPostStems.contains(keywordStem))
      return true
    else
      return false
  }
}

object KeywordBayesClassifier {

  val bayesClassifier: BayesClassifier[String, Boolean] = new BayesClassifier[String, Boolean]()
  val csv: BufferedSource = io.Source.fromFile("src/resources/learning_data.csv")
  csv.getLines().foreach(rawLine => {
    val learningData = rawLine.split(",").map(_.trim)
    val learningWords = learningData(0).split("\\s").toList.asJava
    bayesClassifier.learn(learningData(1).toBoolean, learningWords)
  })
}
