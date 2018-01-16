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
      val stringOfTags = pattern.+:(" ").reduce(_ + " " + _).replaceFirst(" ", "").replaceFirst(" ", "")//todo: zabezpieczenie przed nullem
      val listOfTagsOfSentence: List[String] = stemmedSentence.values.map(tag => tag.split(":")(0)).toList
      listOfTagsOfSentence match {
        case some :: `stringOfTags` :: rest => true
        case _ => false
      }
    }).exists(b => b)

    //wyciagnij wnioski z tego zdania, jezeli pasuje do wzorca,
    // zwroc i wyslij w Content, jezeli nie, wyslij pusta liste bo indexer i tak nie zapisze tego do bazy

    /*tutaj piszemy reguly do filtrowania postow
    np. "Pogoda na rysach jest ostatnio kiepska"
     1. Rzeczownik/Podmiot (pogoda, droga, warunki itd.)
     2. Miejscownik (ze słowem kluczowym)
     3. Czasownik/Orzeczenie (jest)
     4. Okolicznik czasu (ostatnio, niedawno, wczoraj)
     5. Przymiotnik (dobra, zła, kiepska itd.)

     może po prostu kilka wzorców sobie przygotować i dopasowywać zdania do nich, jak spełniają reguły to zapisywać
     jak nie to odrzucać

    */

  }

}
