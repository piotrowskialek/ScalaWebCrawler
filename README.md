# ScalaWebCrawler
inzynierka

# todo
* Zebranie wyników o zależnościach wydźwięku i emocji w stosunku do tego czy treść z forum pochodzi od komentującego lub pierwszej osoby zakładającej nowy wątek. 
W ramach tego przebudowa architektury i modelu danych:
    1. W bazie zamiast postu i zbioru cech emocji trzeba trzymać post, listę skojarzonych komentarzy i wydźwięki każdego z nich
    2. Trzeba będzie wykrywać stronę z nagłówkami tematów, wchodzić w każdy z nich zapisując OP i komentarze i dalej tak samo jak teraz. Nie wiem jak to zrobić sensownie do pomyślenia.

    
    {
        "original-poster": {
            "post-text": "string",
            "emotion": "string"
        },
        "list-of-comments": [{
            "post-text": "string",
            "emotion": "string"
        }]
    }
    
* zrobic klasyfikator bayesa z biblioteki com.github.ptnplanet i porównać z aktualnymi regułami stemingowymi