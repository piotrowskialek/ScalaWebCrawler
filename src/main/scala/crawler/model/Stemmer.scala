package crawler.model

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap

class Stemmer(stemmer: PolishStemmer) extends KeywordContainerPredicate {

  val keywords: List[String] = io.Source.fromFile("src/resources/keywords.csv").getLines().toList
  val setOfStringPatterns: Set[String] = io.Source.fromFile("src/resources/string_patterns.csv").getLines()
    .map(s => s.replace("X", keywords.head)).toSet

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
    return stemmedSentence //mapa (słowo -> stemy)
  }

  override def evaluateKeyWordPredicate(post: String): Boolean = {

    val associatedKeywords: List[String] = getAssociatedKeywords(post)
    if (associatedKeywords.isEmpty)
      return false

    val stemmedSentence: Map[String, String] = parse(post)
    val tagsOfPartsOfSpeech: Set[List[String]] = setOfStringPatterns.map(s => s.split(" ").toList.map(w => getTags(w).split(":")(0)))

    tagsOfPartsOfSpeech.map(pattern => {

      var stringOfTags = pattern.+:(" ").reduce(_ + " " + _)//zabezpieczenie przed nullem
      while (stringOfTags.startsWith(" "))
        stringOfTags = stringOfTags.replaceFirst(" ","")
      stringOfTags = stringOfTags.replaceAll("  ", " ")

      val stringOfTagsPattern = stringOfTags
      val stringOfTagsOfSentence: String = stemmedSentence.values.map(tag => tag.split(":")(0)).toList
        .filter(w => !w.equals("") || !w.replaceAll(" ", "").isEmpty)
        .mkString(" ")

      stringOfTagsOfSentence.contains(stringOfTagsPattern)

    }).exists(b => b)

  }

  def checkIfAdjective(word: Map[String, String]): Boolean =
    if (word.head._2.contains("adj")) true
    else false

  def getAssociatedKeywords(post: String): List[String] = {

    val listOfPostStems: Seq[String] = post.split(" ")
      .map(word => stemmer.lookup(word).asScala
        .map(_.getStem)
        .map(_.toString)
        .map(_.toLowerCase(new Locale("pl")))
        .map(deAccent)
        .headOption
        .getOrElse("")) //todo check if head or foldLeft is better
//        .foldLeft("")(_ + "/" + _)

    return keywords.intersect(listOfPostStems)
  }

  def deAccent(str: String): String = {
    val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("ł", "l").replaceAll("Ł", "L")
  }

}
