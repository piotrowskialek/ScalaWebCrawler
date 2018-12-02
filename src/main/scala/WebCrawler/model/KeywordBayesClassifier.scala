package WebCrawler.model

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier

import scala.collection.JavaConverters._

class KeywordBayesClassifier() extends KeywordContainerPredicate {

  import KeywordBayesClassifier.bayesClassifier

  override def evaluateKeyWordPredicate(post: String): Boolean = {
    bayesClassifier.classify(List(post).asJava).getCategory
  }
}

object KeywordBayesClassifier {

  val bayesClassifier: BayesClassifier[String, Boolean] = new BayesClassifier[String, Boolean]()

  //wczytaj z csv do Map[String, Boolean]
  val learningMap: Map[String, Boolean] = Map[String, Boolean](
    "Pogoda na rysy jest kiepska" -> true,
    "Pozdrawiam mame" -> false,
    "Jaki masz model zasilacza" -> false,
    "Na rysach ostatnio była kiepska pogoda" -> true,
    "Na telefonie mam rysy" -> false,
    "małe rysy na miarę możliwości naszej jury" -> true,
    "asewrgertd asdasda sdasdeferg" -> false,
    "fatalna pogoda opóźniła naszą podróż" -> true
  )
  learningMap.foreach(pair => {
    bayesClassifier.learn(pair._2, pair._1.split("\\s").toList.asJava)
  })

}
