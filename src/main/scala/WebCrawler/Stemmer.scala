package WebCrawler

import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._
import scala.collection.breakOut

class Stemmer(stemmer: PolishStemmer, keyword: String) {

  def getTags(word: String): String = stemmer.lookup(word).asScala
    .map(wd => wd.getTag.toString)
    .+:("")
    .reduce(_ + "/" + _)
    .replaceFirst("/", "")

  def parse(sentence: String): Map[String, String] = {

    val words = sentence.split(" ")
    val tags = words.map(w => getTags(w))
    val stemmedSentence: Map[String, String] = (words zip tags)(breakOut)
    return stemmedSentence
  }

  def keywordPredicate(sentence: String): Boolean = {

    val stemmedSentence: Map[String, String] = parse(sentence)

    true
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
