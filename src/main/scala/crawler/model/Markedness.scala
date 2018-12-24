package crawler.model

object Markedness extends Enumeration {
  type Markedness = Value
  val POSITIVE, NEGATIVE, NEUTRAL = Value
  implicit def enum2String(enum: Markedness.Value): String = enum.toString()
}
