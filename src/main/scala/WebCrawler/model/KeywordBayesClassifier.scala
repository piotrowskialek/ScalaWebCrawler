package WebCrawler.model

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier

import scala.collection.JavaConverters._
import scala.io.BufferedSource

class KeywordBayesClassifier() extends KeywordContainerPredicate {

  import KeywordBayesClassifier.bayesClassifier

  override def evaluateKeyWordPredicate(post: String): Boolean = {
    bayesClassifier.classify(List(post).asJava).getCategory
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
