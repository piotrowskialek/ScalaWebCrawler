package crawler

import java.net.URL

import scala.language.implicitConversions

/**
  * Created by apiotrowski on 14.10.2017.
  */
package object actors {
  implicit def string2url(s: String): URL = new URL(s)
  implicit def string2urlWithSpec(s: (String, String)): URL = new URL(new URL(s._1), s._2)
}