package WebCrawler

import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._

class Stemmer(stemmer: PolishStemmer) {

  def getStems(word: String): String = stemmer.lookup(word).asScala
    .map(wd => wd.getStem.toString)
    .+:("")
    .reduce(_ + "/" + _)
    .replaceFirst("/", "")

  def parse(sentence: String): String = {

    sentence match {
      case "" => ""
    }

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
