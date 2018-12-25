package crawler.model

import morfologik.stemming.polish.PolishStemmer
import org.scalatest.FlatSpec


class StemmerTest extends FlatSpec {


  val listOfTestCases = List("Pogoda na rysy jest kiepska",
    "Pozdrawiam mame",
    "Jaki masz model zasilacza",
    "Na rysach ostatnio była kiepska pogoda",
    "Na telefonie mam rysy",
    "małe yosemite na miarę możliwości naszej jury",
    "asewrgertd asdasda sdasdeferg"
  )
  val stemmer = new Stemmer(new PolishStemmer())

  listOfTestCases(0) should "be true" in {
    assert(stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(0))._1)
  }

  listOfTestCases(1) should "be false" in {
    assert(!stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(1))._1)
  }

  listOfTestCases(2) should "be false" in {
    assert(!stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(2))._1)
  }

  listOfTestCases(3) should "be true" in {
    assert(stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(3))._1)
  }

  listOfTestCases(4) should "be false" in {
    assert(!stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(4))._1)
  }

  listOfTestCases(5) should "be false" in {
    assert(!stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(5))._1)
  }

  listOfTestCases(6) should "be false" in {
    assert(!stemmer.checkSenseAndGetAssociatedKeywords(listOfTestCases(6))._1)
  }

  "ładny" should "be adj" in {
    assert(stemmer.checkIfAdjective(stemmer.parse("ładny")))
  }

  "głowa" should "not be adj" in {
    assert(!stemmer.checkIfAdjective(stemmer.parse("głowa")))
  }

  "getAssociatedKeywords" must "return List(\"rysy\")" in {
    assertResult(List("rysy")) {
      stemmer.getAssociatedKeywords("Rysy są ładne")
    }
  }

  "getAssociatedKeywords called with weird strings" must "return List(\"rysy\")" in {
    assertResult(List("rysy")) {
      stemmer.getAssociatedKeywords("Rysy są ładne asdaisbdhsbeufh")
    }
  }

  "deAccent" must "return SZOLACEZaoleacnN" in {
    assertResult("SZOLACEZaoleacnN") {
      stemmer.deAccent("ŚŻÓŁĄĆĘŹąółęąćńŃ")
    }
  }

}
