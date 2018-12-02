package WebCrawler.model

import de.daslaboratorium.machinelearning.classifier.Classification
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class ClassifierTest extends FlatSpec {

  val classifier = new BayesClassifier[String, Boolean]()

  val learningMap: Map[String, Boolean] = Map[String, Boolean](
    "Pozdrawiam mame" -> false,
    "Jaki masz model zasilacza" -> false,
    "Na telefonie mam rysy" -> false,
    "asewrgertd asdasda sdasdeferg" -> false,
    "Pogoda na rysy jest kiepska" -> true,
    "Na rysach ostatnio była kiepska pogoda" -> true,
    "małe rysy na miarę możliwości naszej jury" -> true,
    "fatalna pogoda opóźniła naszą podróż" -> true
  )
  learningMap.foreach(pair => {
    classifier.learn(pair._2, pair._1.split("\\s").toList.asJava)
  })


  "classifier" should "classify test strings to categories described in testingMap" in {

    val testingMap: Map[String, Boolean] = Map[String, Boolean](
      "Pogoda na rysy jest fatalna" -> true,
      "Pozdrawiam serdecznie" -> false,
      "jaki masz komputer i zasilacz" -> false,
      "Na rysach ostatnio była super pogoda" -> true,
      "Na tablecie mam rysy" -> false,
      "rysy na miarę naszych możliwości" -> true,
      "xadxsdaxsxasd xD asewrgertd asdasda sdasdeferg" -> false
    )

    testingMap.foreach(pair => {
      val classification: Classification[String, Boolean] = classifier.classify(pair._1.split("\\s").toList.asJava)
      println("feature set " + classification.getFeatureset)
      println("prob " + classification.getProbability)
      println("category " + classification.getCategory)
      assert(pair._2.equals(classification.getCategory))
    })
  }



}
