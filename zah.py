from enum import Enum


# model
class StanowiskoPracownika(Enum):
    DOKTORANT = 1
    ADIUNKT = 2
    PROFESOR = 3


class TypJednostkiDyd(Enum):
    WYKLAD = 1
    LAB = 2
    PROJ = 3


class Dziedzina(Enum):
    MAT = 1
    FIZ = 2
    CHEM = 3
    POL = 4


class Przedmiot:
    def __init__(self, nazwa, dziedzina, typ, liczba_godzin):
        self.liczba_godzin = liczba_godzin
        self.typ = typ
        self.dziedzina = dziedzina
        self.nazwa = nazwa


class Pracownik:
    def __init__(self, stanowisko, dziedzina):
        self.stanowisko = stanowisko
        self.dziedzina = dziedzina
        self.pensja = Pensje[stanowisko]
        self.max_godzin = max_working_hours


class JednostkaDydaktyczna:
    def __init__(self, dziedzina, liczba_godzin, typ, nazwa, nauczyciel):
        self.nauczyciel = nauczyciel
        self.dziedzina = dziedzina
        self.liczba_godzin = liczba_godzin
        self.typ = typ
        self.nazwa = nazwa


Jakosc = {
    StanowiskoPracownika.DOKTORANT: 1,
    StanowiskoPracownika.ADIUNKT: 2,
    StanowiskoPracownika.PROFESOR: 4
}

Pensje = {
    StanowiskoPracownika.DOKTORANT: 1,
    StanowiskoPracownika.ADIUNKT: 2,
    StanowiskoPracownika.PROFESOR: 4
}


class MapaDopasowanPracownikPrzedmiot:
    def __init__(self):
        self.mapa = {}

    def dodaj_pare(self, przedmiot, pracownik):
        self.mapa[przedmiot] = pracownik

    def pobierz_przypisanego_pracownika(self, przedmiot):
        return self.mapa[przedmiot]


class Solution:
    def __init__(self, alfa, beta, wsp_pasujacej_jakosci, wsp_niepasujacej_jakosci):
        self.wsp_pasujacej_jakosci = wsp_pasujacej_jakosci
        self.wsp_niepasujacej_jakosci = wsp_niepasujacej_jakosci
        self.beta = beta
        self.alfa = alfa
        self.mapa_dopasowan_przedmiot_pracownik = {}

    def dodaj_dopasowanie(self, przedmiot, pracownik):
        self.mapa_dopasowan_przedmiot_pracownik[przedmiot] = pracownik

    def pobierz_przypisanego_pracownika(self, przedmiot):
        return self.mapa_dopasowan_przedmiot_pracownik[przedmiot]

    def oblicz_jakosc_pracownika(self, pracownik, przedmiot):
        if przedmiot.dziedzina == pracownik.dziedzina:
            mnoznik_jakosci = self.wsp_pasujacej_jakosci
        else:
            mnoznik_jakosci = self.wsp_niepasujacej_jakosci
        jakosc = mnoznik_jakosci * Jakosc[pracownik.stanowisko]
        return jakosc

    def funkcja_jakosci(self, przedmioty, pracownicy):
        jakosc = 0
        for (przedmiot, pracownik) in self.mapa_dopasowan_przedmiot_pracownik:
            jakosc = jakosc + self.oblicz_jakosc_pracownika(pracownik, przedmiot)
        return jakosc

    def funkcja_kosztu(self, przedmioty, pracownicy):
        koszt = 0
        for pracownik in set(self.mapa_dopasowan_przedmiot_pracownik.values()):
            koszt = koszt + pracownik.pensja

    # funkcja optymalizowana, to pewnie do jakiegos optymalizatora trzeba machnac
    def fun_celu(self, jakosc, koszt):
        return self.alfa * jakosc + self.beta * koszt


# wartosci wejsciowe
max_working_hours = 140  # maksymalna liczba godzin dla pracownika
max_budget = 100000  # maksymalny budzet uczelni

Przedmioty = [
    Przedmiot(nazwa="POBO", dziedzina="infa", typ="laby", liczba_godzin=4),
    Przedmiot(nazwa="POBO", dziedzina="infa", typ="wyklad", liczba_godzin=2),
    Przedmiot(nazwa="SKM", dziedzina="matma", typ="laby", liczba_godzin=4),
    Przedmiot(nazwa="ZAH", dziedzina="chemia", typ="wyklad", liczba_godzin=3),
    Przedmiot(nazwa="ZAH", dziedzina="chemia", typ="laby", liczba_godzin=5),
    Przedmiot(nazwa="SNR", dziedzina="chemia", typ="laby", liczba_godzin=2),
    Przedmiot(nazwa="SNR", dziedzina="chemia", typ="wyklad", liczba_godzin=4),
    Przedmiot(nazwa="ZPI", dziedzina="fizyka", typ="laby", liczba_godzin=3),
    Przedmiot(nazwa="ZPI", dziedzina="fizyka", typ="wyklad", liczba_godzin=2)
]

# wartosci wyjsciowe


if __name__ == '__main__':
    a = Solution(2, 2, 1, 0.1)
    print(a.oblicz_jakosc_pracownika(Pracownik(StanowiskoPracownika.PROFESOR, Dziedzina.POL), Przedmioty[0]))
