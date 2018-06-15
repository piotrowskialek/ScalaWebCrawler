package WebCrawler.model


import java.util

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import org.scalatest.FlatSpec

class ClassifierTest extends FlatSpec {

  val classifier = new BayesClassifier[String, String]()

  val listOfTestCases = List("Pogoda na rysy jest kiepska",
    "Pozdrawiam mame",
    "Jaki masz model zasilacza",
    "Na rysach ostatnio była kiepska pogoda",
    "Na telefonie mam rysy",
    "małe yosemite na miarę możliwości naszej jury",
    "asewrgertd asdasda sdasdeferg"
  )

  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases.head))

  classifier.learn("nie", util.Arrays.asList(listOfTestCases(1)))
  classifier.learn("nie", util.Arrays.asList(listOfTestCases(2)))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases(3)))
  classifier.learn("nie", util.Arrays.asList(listOfTestCases(4)))
  classifier.learn("tak", util.Arrays.asList(listOfTestCases(5)))
  classifier.learn("nie", util.Arrays.asList(listOfTestCases(6)))
  classifier.learn("nie", util.Arrays.asList(listOfTestCases(6)))


  println(classifier.classify(util.Arrays.asList("Pogoda na rysy jest super")).getCategory)

}
