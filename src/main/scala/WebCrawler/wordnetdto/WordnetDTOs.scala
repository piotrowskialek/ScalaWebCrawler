package WebCrawler.wordnetdto

case class results(synset: List[synset], href: String)
case class synset(str: String, related: related, definition: String, units: List[unit], id: Int, is_artifficial: Boolean)
case class related(`synonimia_międzyjęzykowa/Syn_plWN-PWN`: String)
case class unit()