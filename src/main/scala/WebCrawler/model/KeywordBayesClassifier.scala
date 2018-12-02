package WebCrawler.model

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier

import scala.collection.JavaConverters._

class KeywordBayesClassifier(bayesClassifier: BayesClassifier[String, Boolean]) extends KeywordContainerPredicate {

  val listOfTestCases = List("Pogoda na rysy jest kiepska",
    "Pozdrawiam mame",
    "Jaki masz model zasilacza",
    "Na rysach ostatnio była kiepska pogoda",
    "Na telefonie mam rysy",
    "małe yosemite na miarę możliwości naszej jury",
    "asewrgertd asdasda sdasdeferg"
  )

  bayesClassifier.learn(true, List(listOfTestCases(1)).asJava)
  bayesClassifier.learn(false, List(listOfTestCases(2)).asJava)
  bayesClassifier.learn(false, List(listOfTestCases(3)).asJava)
  bayesClassifier.learn(true, List(listOfTestCases(4)).asJava)
  bayesClassifier.learn(false, List(listOfTestCases(5)).asJava)
  bayesClassifier.learn(true, List(listOfTestCases(6)).asJava)
  bayesClassifier.learn(false, List(listOfTestCases(6)).asJava)

  //todo learning
  override def evaluateKeyWordPredicate(post: String): Boolean = {
    bayesClassifier.classify(List(post).asJava).getCategory
  }
}
