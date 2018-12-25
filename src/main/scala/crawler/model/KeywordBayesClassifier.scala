package crawler.model

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import morfologik.stemming.polish.PolishStemmer

import scala.collection.JavaConverters._
import scala.io.BufferedSource

class KeywordBayesClassifier(stemmer: PolishStemmer) extends KeywordContainerPredicate {

  import KeywordBayesClassifier.{bayesClassifier, keywords}

  override def checkSenseAndGetAssociatedKeywords(post: String): (Boolean, List[String]) = {

    val associatedKeywords: List[String] = getAssociatedKeywords(post)
    if (associatedKeywords.isEmpty)
      return (false, associatedKeywords)
    val result: Boolean = bayesClassifier.classify(List(post).asJava).getCategory
    return (result, associatedKeywords)
  }

  override def getAssociatedKeywords(post: String): List[String] = {

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

object KeywordBayesClassifier {

  val keywords: List[String] = io.Source.fromFile("src/resources/keywords.csv").getLines().toList
  val bayesClassifier: BayesClassifier[String, Boolean] = new BayesClassifier[String, Boolean]()
  val csv: BufferedSource = io.Source.fromFile("src/resources/learning_data.csv")
  csv.getLines().foreach(rawLine => {
    val learningData = rawLine.split(",").map(_.trim)
    val learningWords = learningData(0).split("\\s").toList.asJava
    bayesClassifier.learn(learningData(1).toBoolean, learningWords)
  })
}
