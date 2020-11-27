# order-api
webshop orders' lifecycle support

Feladat
Beadási határiő: 2020.december.18
Formátum: GitHub repository URL-t küldeni


Implementálj egy megrendelt termék szállítását nyomonkövető tracking rendszert.

Készíts el az alábbi funkcionalitást lehetővé tevő API-t!

- A rendszer segít a megrendelt termékek életciklusának követésében.
- A felhasználó megvásárolhat termékeket az alkalmazáson keresztül. A vásárláskor
az előre definiált termékek köztül a raktáron lévő mennyiség erejéig vásárolhat. A vásárlás befejezése egy order, magyarul
rendelés formájában manifesztálódik.
- A vásárláskor kiválaszthatja a kézbesítési módot, a fizetési módot, illetve természetesen meg kell
adja a vevő elérhetőségeit: lackím, telefonszám, név, email cím.
- Helyszíni vásárlás esetén (a vevő a boltban vásárol, akár készpénzzel, kártyával) a rendelés azonnal DELIVERED státuszba kerül.
- Amennyiben online történik a vásárlás, a rendelés BOOKED állapotba kell kerüljön.
- A vevő online rendelés esetén, amennyiben házhoz szállítást kért, miután a futár átvette a rendelt termékeket a rendelés azonnal IN_PROGRESS
státuszba kell kerüljön
- Amennyiben a házhozszállítás megtörténik, a rendelés DELIVERED státuszba kell kerüljön
- Amennyiben nem sikerül a házhozszállítás, a rendelés FAILED_DELIVERY státuszba kell kerüljön és a futárnak lehetőséget
kell adni (kötelezően), hogy kommentet fűzhessen a rendeléshez a sikertelenség okáról. (nem vették át, nem voltak otthon...) 


A beadás követelményei:

- Maven projekt
- Legyenek unit tesztek írva a főbb ágakhoz
- GitHub repository URL-t kell elküldenetek


Köszi,

Attila

---------------------------------------------------------------------
Terv...
Termék (Product) (cikkszám, megnevezés, egységár, ÁFA%)
Terméktétel (Lot)(Product, mennyiség)
Raktári tétel (StocItem <-- Lot)((Lot), összérték)
Raktár (Stock) (Raktári tételek(Terméktétel) listája (Map), bevételezés, kiadás)
Felhasználó (Vevő - Customer)(lackím, telefonszám, név, email cím)
Rendeléstétel (OrderItem <-- Lot)((Lot), nettó érték, ÁFA-érték, összérték)
Megrendelés (Order)(Rendeléstételek(Rendeléstétel), átvételi mód, fizetési mód, Vevő, állapot, sikertelenségi megjegyzés(failure_comment))
Megrendelés-állapotok (DELIVERED, BOOKED, IN_PROGRESS, FAILED_DELIVERY)
