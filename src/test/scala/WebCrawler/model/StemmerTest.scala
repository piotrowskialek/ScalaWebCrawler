package WebCrawler.model

import morfologik.stemming.polish.PolishStemmer
import org.scalatest.FlatSpec


class StemmerTest extends FlatSpec{


  val listOfTestCases = List("Pogoda na rysy jest kiepska",
    "Pozdrawiam mame",
    "Jaki masz model zasilacza",
    "Na rysach ostatnio była kiepska pogoda",
    "Na telefonie mam rysy",
    "małe yosemite na miarę możliwości naszej jury",
    "asewrgertd asdasda sdasdeferg"
  )
  val stemmer = new Stemmer(new PolishStemmer(), "rysy")

  listOfTestCases(0) should "be true" in {
    assert(stemmer.evaluateKeyWordPredicate(listOfTestCases(0)))
  }

  listOfTestCases(1) should "be false" in {
    assert(!stemmer.evaluateKeyWordPredicate(listOfTestCases(1)))
  }

  listOfTestCases(2) should "be false" in {
    assert(!stemmer.evaluateKeyWordPredicate(listOfTestCases(2)))
  }

  listOfTestCases(3) should "be true" in {
    assert(stemmer.evaluateKeyWordPredicate(listOfTestCases(3)))
  }

  listOfTestCases(4) should "be false" in {
    assert(!stemmer.evaluateKeyWordPredicate(listOfTestCases(4)))
  }

  listOfTestCases(5) should "be false" in {
    assert(!stemmer.evaluateKeyWordPredicate(listOfTestCases(5)))
  }

  listOfTestCases(6) should "be false" in {
    assert(!stemmer.evaluateKeyWordPredicate(listOfTestCases(6)))
  }

  "ładny" should "be adj" in {
    assert(stemmer.checkIfAdjective(stemmer.parse("ładny")))
  }
  "głowa" should "not be adj" in {
    assert(!stemmer.checkIfAdjective(stemmer.parse("głowa")))
  }
}
