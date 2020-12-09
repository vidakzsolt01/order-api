# hu.gov.allamkincstar.training.javasebsc.orderapi.order-api
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
Terv
-
- Az Order alapja a Product: cikkszám (unique ID), megnevezés, nettó egységár, ÁFA%
- Ha Product-ot tárolunk, azt nem önmagában, hanem mennyiségével együtt tároljuk (bármely tárólóban (Stock (Raktár), Cart (Kosár), Order(Rendelés)) legyen is), ez lesz a ProducItem (TermékTétel) osztály, mely 
  - az adott Product-hoz - természetesen - nyilvántartja saját aktuális darabszámát: quantity,
  - a darabszámot "kívülről" közvetlenül nem engedem módosítani (nem lesz setter-e), tehát (csak) a ProductItem "tudja"  módosítani (növelni és csökkenteni) a darabszámát
  - a csökkentéshez nem elegendő mennyiség managed exceptiont vált ki, ezért azután nem egyszerűen "módosítani" akarom (+/-), a növelésnél ilyen elvárás nincs "elegendőség"-vizsgálat, tehát a <b>throws</b> csak csökkentéskor kell)
  - a ProductItem-et önmagában nem akarom, hogy "csak úgy" példányosítsa a <i>kliens</i> (nincs is értelme), viszont örököltetnem muszáj, ezért abstract osztályt csinálok belőle
- A <i>"vásárláskor az előre definiált termékek köztül a raktáron lévő mennyiség erejéig vásárolhat"</i> nekem azt jelenti, hogy
  - raktárkészletet tárolunk egy Raktár-ban (Stock), és
  - a "vásárláskor" feltöltünk egy Kosarat (Cart), melynek során
    - adott termékből adott mennyiséget kérünk (lefoglalunk) a Raktárból, és
    - ezt hozzáadjuk a Kosár aktuális tartalmához
  - a raktárkezelés nem része a feladatnak, ezért csak minimális funkcionalitással valósul meg
    - a Stock-ban StockItem-eket tárolunk (Product.itemNumber-rel (cikkszám) indexelt listában (Map)), melyek olyan ProductItem-ek, melyeknek van "lefoglalt mennyiség"-ük (bookedQuantity), és tud lefoglalni mennyiségeket (saját magából), meg tudja mondani, hogy menniy foglalható mennyiség van, stb.
    - a Stock-ba be kell tudni tenni Product-okat, melynek során hozzáadunk egy Product-ból képzett ProductItem-et Map-hez (ha létezik már adott ProductItem, akkor csak annak mennyiségét növeljük): deposit(), és
    - le kell tudni foglalni ProductItem-eket a Cart tartalmának bővítéséhez: <b>book()</b> 
      - ha nem létezik az adott Product a Stock ProductItems Map-ben, akkor az algoritmushiba (unmanaged exception kell)
      - ha nincs a lefoglalni kívánt mennyiség készleten, akkor azt az algoritmusban kezelni kell (managed exception kell)
  - a "vásárlás" során feltöltjük - a Stock-hoz lényegileg nagyon hasonlító - Cart osztály specializált ProductItem-eket (CartItem) tartalmazó CartItem-listáját (Map<..., CartItem>) (a CartItem olyan ProductItem, amelynek van nettó és ÁFA összege). Feltöltéskor technikailag a Stock-ból lefoglalt (book) StockItem-eket (ProductItem) hozzáadjuk a Cart CartItem-eihez (ProductItem)
  - a Cart lezárásával (a "vásárlás" lezárásával) (<b>closeCart()</b>) készül az Order
  
- Order
  -
  - alapja az OrderItem-ek listája a rendelt tételeket a Cart elemeiből (CartItems) a Cart vásárlást záró metódusa (closeCart()) hozza létre
  -egy OrderItem olyan ProductItem, amelynek van nettó összege és ÁFA összege (vagyis gyakorlatilag ugyanaz, mint a CartItem) 
  - vannak olyan Webshopok, amelyek a rendelés feladása után, útólag is megengedik módosítani a rendelés összetételét, de ez valszeg a rossz programtervezés eredménye (pl. a rendelés feladásakor derül ki, hogy még sincs (elegendő) raktáron a lefoglalt/megrendelt termékből). Itt ezt nem tesszük, ezért az Order OrderItem-listása egy sima List\<OrderItem\> lesz 
  - további Order-adatok:
    - properties
      - vásárlási mód (Enum(DIRECT, ONLINE))
      - nettó összeg (netSum)
      - ÁFA összeg (VATSum)
      - bruttó összeg (grossSum)
      - számlaösszeg (billTotal)
      - átvételi mód (deliveryMode; Enum(DIRECT_RECEIVING, DELIVERY_SERVICE)
      - fizetési mód (paymentMode; Enum(CASH, BY_WIRE, CREDIT_CARD)) (CASH ONLINE vásárlás esetén "utánvét"-et jelöl)
      - Vevő (Customer: név, telefonszám, email cím, számlázási cím, szállítási cím)
    - metódusok
      - 
  - kétféle Order-ünk van
    - személyes bolti vásárláshoz (közvetlen): OrderDirect, és
    - online vásárláshoz: OrderOnline
    - a kettő lényegileg elsősorban a szállításban különbözik: az egyiknek tudni kell, a másiknak nem, ezért a közös elemeket kiemelem egy ős Order-be
      - tehát az Order
      - (vásárlási mód tulajdonság feleslegessé válik, megszüntetem)
      - nettó összeg (netSum)
      - ÁFA összeg (VATSum)
      - bruttó összeg (grossSum)
      - számlaösszeg (billTotal)
      - (állapot értékkészlete eltérő a kétféle Order-ben, ezért az ősben felesleges, megszűntetem)
  - OrderDirect
    - állapot (orderStatus; Enum(BOOKED, DELIVERED) - default: BOOKED)
  - OrderOnline
    - állapot (orderStatus; Enum(PENDING, BOOKED, WAITING_FOR_DELIVERY, IN_PROGRESS, DELIVERED, FAILED_DELIVERY) - default: PENDING)
    - szállítási paraméterek (DeliveryParameters (szállítási költség (deliveriCharge), összeghatár (limitForFree))
    - megjegyzés (failureComment; FAILED_DELIVERY státusz esetén kötelező))
  - rendelésfeladás - doOrder(): 
    - ha vásárlási mód ONLINE, akkor az állapot BOOKED lesz
    - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: közvetlen bolti vásárlás esetén nem lehet feladni a rendelést")
  - fizetés nyugtázása - paymentConfirm():
    - ha a státusz BOOKED, akkor 
      - ha a vásárlási mód ONLINE, akkor a státusz WAITING_FOR_DELIVERY lesz
      - egyébként a státusz DELIVERED lesz
  - átadás a futárszolgálatnak - passToDeliveryService():
    - ha a státusz NEM WAITING_FOR_DELIVERY, akkor hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: a rendelés nem kész a futárnak való átadásra")
    - ha vásárlási mód ONLINE, akkor
      - ha a fizetési mód BY_WIRE vagy CREDIT_CARD (v. nem CASH)
        - ha a számla ki van fizetve (Order.paid = true), akkor az állapot IN_PROGRESS lesz
        - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: nem készpénzes vásrlás esetén amíg a számla nincs kiegyenlítve, nem adható át a futárnak")
      - egyébként az állapot IN_PROGRESS lesz
    - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: közvetlen vásárlás esetén nem adható át a futárnak")
  - szállítás nyugtázása - deliveryConfirm(boolean success, (optional) String failureComment):
    - ha az állapot IN_PROGRESS, akkor 
      - ha a szállítás sikeres (success == true) az állapot DELIVERED lesz
      - egyébként
        - ha kaptunk failureComment-et, akkor 
          - az állapot FAILED_DELIVERY lesz
          - beállítjuk az Order.failureComment-et
        - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: sikertelen átvétel esetén a sikertelenség oka nélkül a szállítás nem nyugtázható")  
    - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható")
  - rendelés lezárása - orderClose():
    - ha a státusz DELIVERED vagy FAILED_DELIVERY, akkor véglegesítjük a raktárkészlet-változást
    - egyébként hiba: InvalidOrderOperationException (managed exception: "Érvénytelen művelet: nem véglegesített rendelés nem zárható le.")
- A Stock-ban StockItem-eket (Raktártétel-ek)) tárolunk, mely a ProductItem-ból (Terméktétel) származnak: a StockItem olyan ProductItem, 
  - melynek van "lefoglalt mennyiség" (bookedQuantity) property-je, és
  - tud mennyiségeket lefoglalni, felszabadítani
- Felmerül, hogy a StockItem a Stock teljes magánügye, nem akarom, hogy lehessen önállóan, a Stock-on kívül implementálni, ezért a Stock inner class-a lesz, de ha le kell kérni a raktárkészletet (StockIteme-ek listája), akkor a StockItem típusnak látszania kell "kívül" is: inner class elvetve
- Elviekben - a StocItem mintájára - a Cart CartItemeket, az Order pedig OrterItemeket tartalmaz, de... Miután - a "közös" ProductItem-hoz képest - mindkettő ugyanolyan <i>saját</i> tulajdoságokkal bír (nettó összeg, ÁFA összeg), eltekintek két külön osztály implementációjától, s mind a Cart-ban, mind az Order-ben ugyanazt az OrderItem típust fogom tárolni
- A Cart és a Stock - miután mindkettő módosítható, ProductItem-ekat tároló elem (container) - sok hasonló tevékenységgel bírnak, 
  - célszerűnek látszik egy interfac-szel (ProductContainerHandler) előírni a közös viselkedést, és
  - deklarálni egy közös szülő osztályt (ProductContainer)
- Első ránézésre az Order is ProductContainer, de valójában itt nem akarom (nem engedem) módosítani a tárolt elemeket (nem releváns a tárolókénti viselkedés), elegendő lesz csak List, és nem szükséges (tehát: nem szabad) a ProductContainer-ből leszáraznia
- A tárolókban (Stock, Cart) elérhetők az ősosztály azon mezői, amelyeket a tárolókon keresztül nem módosítok közvetlenül, és nem akarom, hogy ezt az API-t használó (kliens) megtehesse egy leszármazáson keresztül, ezért a tárolók <b>final</b> osztályok
- Előbbi kitétel a "tárolókban" (Stock, Cart) tárolt terméklistát (pontosan: sem a listát, sem a lista bármely elemét) fokozottan érinti, mert magát a terméklistát "láthatóvá" kell tenni, tehát erre további intézkedés kell, miszerint 
  - a terméklista <b>private</b> lesz
  - lista tartalmát szolgáltató metódus egy olyan List-et fog visszaadni, mely az eredeti listában tárolt elemek <i>másolatát</i> tartrtalmazza
- Előbbieket érvényesítem a - nem tényleges "tároló" - Order-re is
- A fizetési és szállítási módok, a vásárlási módok és a rendelés-állapotok Enum-ok
------
