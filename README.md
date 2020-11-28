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
- Alaplényeg a Termék (Product)
- Kell Terméktétel, mely adott Termékhez nyilvántartja annak darabszámát: Lot(Product, quantity)
- A raktárkészletet a Raktár-ban (Stock) tároljuk - Raktári tételek indexelt listájában (Map)
- Egy Raktári tételt (StockItem) a Terméktételből származtatjuk, melynek van pl. összértéke
- Felhasználó kell majd a rendeléshez (Vevő - Customer)(név, telefonszám, email cím, számlázási cím, szállítási cím)
- Fizetési mód kell a rendeléhez; elfogadott fizetési módok: CASH, BY_WIRE, CREDIT_CARD
- Szállítási mód kell a rendeléshez; elfogadott szállítási módok: DIRECT_RECEIPT, DELIVERY_SERVICE
- Rendelésben (Order) a rendelt tételeket (OrderItem) a Termék cikkszámával indexelt listában (Map) tartjuk nyilván; további adatok: vásárlási mód, nettó összeg, ÁFA összeg, bruttó összeg, átvételi mód, fizetési mód, szállítási mód, Vevő, állapot, szállítás eredménye, megjegyzés(failure_comment))
- Rendeléstétel (OrderItem) olyan Terméktétel, amelynek van nettó értéke, ÁFA-értéke, összértéke;
- Rendelés-állapotok (BOOKED, IN_PROGRESS, DELIVERED, FAILED_DELIVERY)
------

Implementáció
- Raktártételek és Rendeléstételek voltaképpen Terméktételek - további property-kkel
- Raktár és Rendelés viselkedése sok részben hasonló (tétel felvétele, kiadás(kivétel), terméktétel mennyiségének módosítása, stb), ezért alkalmazunk a közös műveletek előírásához egy Interface-t (ContainerHandler)
- Raktár és Rendelés szerkezete is hasonló (indexelt lista a tárolt tételekről), ezért deklarálunk egy szülő osztályt (Container), melyből mindkettőt származtatjuk majd
- A fizetési és szállítási módok és a rendelés-állapotok Enum-ok  
Osztályok:
- Product (Termék)
  - properties:
    - cikkszám (itemNumber), final String
    - megnevezés (itemName), final String
    - nettó egységár (netUnitPrice), Integer
    - ÁFA%, Integer
  - metódusok: -
- Lot (Terméktétel)
  - properties:
    - Termék, final Product 
    - mennyiség (quantity), Integer 
    - index, final String (miután a terméklistákban a termék cikkszáma az index, ezt ide kiemeljük a termékből (Product.itemNumber)
  - metódusok:
    - mennyiség módosítása, void changeItemQuantity(Integer ) throws NotEnoughItemException (Exception)
- ContainerHandler (Interface)
  - metódusok
    - új terméktétel felvétele, void registerNewItem(Lot item)
    - tétel keresése, void findItem(Lot lot)  throws NoItemFounException (RuntimeException)
    - terméktétel törlése, void removeItem(Lot lot) throws NoItemFounException (RuntimeException)
    - terméktétel mennyiségének módosítása, void changeItemQuantity(Lot item, Integer quantity) throws NotEnoughItemException (RuntimeException)
    - "elfogyott" terméktétel kitakarítása,  void disposeEmptyItem(Lot item) throws NoItemFounException (RuntimeException)
- Container (implements ContainerHandler)
  - properties
    - terméklista, final Map<String, Lot> containerItems
  - metódusok
    - lásd ContanerHandler Interface
- DeliveryModeEnum (Enum)
  - DIRECT_RECEIVING, DELIVERY_SERVICE
- OrderStatusEnum (Enum)
  - PENDING, DELIVERED, BOOKED, IN_PROGRESS, FAILED_DELIVERY
- PaymentModeEnum (Enum)
  - CASH, CREDIT_CARD
- Customer
  - properties
    - azonosító (customerID), final Long 
    - név (name), String
    - telefonszám (phoneNumber), String
    - e-mailcím (email), String 
    - szállítási cím (deliveryAddress), String
    - számlázási cím (accountAddress), String
  - metódusok: -
- OrderItem (extends Lot)
  - properties
    - nettó összeg (netAmount), final Integer
    - ÁFA összege (VATAmount), final Integer
    - bruttó összeg (grossAmount), final Integer
  - metódusok: - (lásd Lot)
- Order (extends Container)
  - properties
    - 
- StockItem (extends Lot)
  - 