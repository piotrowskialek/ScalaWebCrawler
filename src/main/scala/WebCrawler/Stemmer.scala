package WebCrawler

import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap

class Stemmer(stemmer: PolishStemmer, keyword: String) {

  val setOfStringPatterns: Set[String] =
    Set("Pogoda na X jest kiepska",
    "Wczoraj na X była kiepska pogoda",
    "Nie warto teraz wchodzć na X",
    "Na X ostatnio była kiepska pogoda",
    "W drodze na X była kiepska pogoda")
    .map(s => s.replace("X", keyword))  //dla wygody

  def getTags(word: String): String = stemmer.lookup(word).asScala
    .map(wd => wd.getTag.toString)
    .+:("")//reduce nie dziala na pustej liscie, musi byc co najmniej jeden element
    .reduce(_ + "/" + _)
    .replaceFirst("/", "")

  def parse(sentence: String): Map[String, String] = {

    val words: Array[String] = sentence.split(" ")
    val tags: Array[String] = words.map(w => getTags(w))
    val tuples: Array[(String, String)] = words zip tags
    val stemmedSentence: ListMap[String, String] = ListMap(tuples: _*)
    return stemmedSentence
  }

  def keywordPredicate(sentence: String): Boolean = {

    val stemmedSentence: Map[String, String] = parse(sentence)
    val tagsOfPartsOfSpeech: Set[List[String]] = setOfStringPatterns.map(s => s.split(" ").toList.map(w => getTags(w).split(":")(0)))

    tagsOfPartsOfSpeech.map(pattern => {

      var stringOfTags = pattern.+:(" ").reduce(_ + " " + _)//todo: zabezpieczenie przed nullem
      while (stringOfTags.startsWith(" "))
        stringOfTags = stringOfTags.replaceFirst(" ","")//todo: trimowanie
      stringOfTags = stringOfTags.replaceAll("  ", " ")

      val stringOfTagsPattern = stringOfTags

//      val stringOfTagsPattern = pattern.filter(w => w.equals("") || w.replaceAll(" ", "").isEmpty)


      val stringOfTagsOfSentence: String = stemmedSentence.values.map(tag => tag.split(":")(0)).toList
        .filter(w => !w.equals("") || !w.replaceAll(" ", "").isEmpty)
        .mkString(" ")

      stringOfTagsOfSentence.contains(stringOfTagsPattern)

    }).exists(b => b)

  }

}
