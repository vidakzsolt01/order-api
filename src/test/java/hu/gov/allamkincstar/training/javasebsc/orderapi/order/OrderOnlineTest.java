package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.DeliveryModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusOnlineEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.StockItem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;

import java.awt.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Az egyes metódusok tesztjeit a megrendelés tervezett életciklusa szerinti
// sorrendben akaraom futtatni (egyik metódus által állított értékeket
// felhasználom a következőben), hogy lássam, hogy ha minden "szabályosan" halad,
// akkor mit mutat a kód.
// A "szabályos" sorrend (egy online, bakkártyás, futárszolgálatos rendelés esetén)
// tehát:
// - dispatchOrder()         - rendelés feladása
// - confirmPayment()        - fizetés megerősítése
// - passToDeliveryService() - átadás a futárszolgálatnak
// - confirmDelivery()       - szállítás/átvétel megerősítése
// - closeOrder()            - rendelés lezárása.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderOnlineTest extends Container {

    // controll message a hibaüzenetek (exceptions) ellenőrzéséhez
    static final String MESSAGE_DEFAULT = "no message";

    // Alapvetés: kell pár Termék
    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 5);

    // az egyszerűség kedvéért általában a tesztek globális alapobjektumokkal
    // operálnak, a speciális esetekhez maj "helybe" deklarálom a szükségeseket
    static OrderOnline order;
    static Customer customer = null;
    static Stock stock;
    static Cart cart;

    @BeforeAll
    static void prolog() {
        //--------------------------------------------------------------------------------
        // A tesztekhez kell egy OrderOnline object, amit a Cart.closeCart()-ja hoz létre,
        // tehát kell  egy Cart.
        // A Cart feltöltését viszont raktárból lehet intézni, tehát kell egy
        // Raktár (Stock) is, kezdem ezzel
        //-----------------------------------------------
        // itt nem a raktárat és a kosarat akarom tesztelni: az exception-ökkel nem foglalkozom...
        //--------------------------------------------------------------------------------
        stock = Stock.getInstance();
        // a raktár singleton, ezért itt kiürítem a helyes teszteredmények
        // ("foglalt", "maradt", stb. készletek) miatt
        for (ProductItem item:stock.productItemList()){
            stock.removeItem(item.getIndex());
        }
        try {
            stock.depositProduct(prod1, 100);
            stock.depositProduct(prod2, 500);
            stock.depositProduct(prod3, 1000);
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // Megvan a Raktár, most feltöltök egy Kosarat a rakétártételekből
        cart = new Cart();
        try {
            cart.addNewProduct(prod1.getItemNumber(), 100, stock);
            cart.addNewProduct(prod2.getItemNumber(), 100, stock);
            cart.addNewProduct(prod3.getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }

        // Végül itt a kezdeti végcél: csinálok egy OrderOnline-t a Cart.closeCart()-tal
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    // az egyes tesztmetódusok hibatesztjeihez időnként kell csinálnom egy-egy
    // új Order-t, ehhez csinálok egy OrderOnline csináló metódust, mely
    // "aktuális" kosártartalomból tud készülni
    private OrderOnline createOrder(Cart cart){
        OrderOnline order = null;
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException | CartClosedException e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * dispatchOrder() - rendelésfeladás
     * Akkor lehetséges, ha
     * - van Customer (!= null) és a kötelező mezői (név, cím, email, telefon) ki vannak töltve
     * - a fizetési mód meg van adva
     * - a szállítási mód meg van adva
     * - a státusz PENDING
     * Ezeket tesztelem itt - kivéve a státuszt, mert az
     * - "védett" mező, nem állítható közvetlenül "kívülről" (tehát itt nem tudok különböző
     *    értékeket adni neki), és
     * - az Order példányosításakor eleve PENDING értéket kap, vagyis nemigen tesztelhető
     *
     * dispatchOrder() BOOKED-ra állítja az Order státuszát
     */
    @Test
    @Order(1)
    void dispatchOrder() {
        // miután az Order épp most jött létre és még nincs Customer, a rendelésfeladás nem mehet,
        // InvalidOrderOperationException-hoz vezet
        String message = MESSAGE_DEFAULT;

        // customer kezdetben null: InvalidOrderOperationException
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Vásárló-adatok nélkül"));

        // hiányos customer-adatok (nincs cím, email és telefon): InvalidOrderOperationException
        customer = new Customer(1L, "Vevő");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // hiányos customer-adatok (nincs email és telefon): InvalidOrderOperationException
        customer.setDeliveryAddress("Cím");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // hiányos customer-adatok (telefon): InvalidOrderOperationException
        customer.setEmail("email@gcim.hu");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // customer-adatok rendben, szállítási mód nincs átadva: InvalidOrderOperationException
        customer.setPhoneNumber("phonenumber");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, null);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Nem választott szállítási módot"));

        // customer-adatok rendben, szállítási mód rendben, fizetési mód
        // hibás: InvalidOrderOperationException
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CASH, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Ez a fizetési mód ennél a megrendeléstípusnál nem választható"));

        // customer-adatok rendben, szállítási mód rendben, fizetési rendben, elvileg feladható
        // (a feladáshoz a státusznak PENDING-nek kell lennie, de a státusz "kívülről" nem
        //  állítható közvetlenül, ezért ez nem tesztelhető innen; egyébként az Order-t kapcsiból
        //  PENDING státusszal példányosítom, úgyhogy az nem lehet rossz), de azért...
        // ezt ellenőrizhetem:
        assertEquals(OrderStatusOnlineEnum.PENDING, order.getOrderStatus());

        try {
            // fizetési mód: bankkártya, átvételi mód: futárszolgálat
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
            message = MESSAGE_DEFAULT;
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        // nem volt hiba?
        assertEquals(MESSAGE_DEFAULT, message);
        // státusz beállt BOOKED-ra?
        assertEquals(OrderStatusOnlineEnum.BOOKED, order.getOrderStatus());
    }

    /**
     * confirmPayment() - fizetés megerősítése
     * - csak akkor fut le, ha BOOKED a státusz (státuszt nem lehet közvetlenül állítani,
     *   ezt nem tudom tesztelni)
     * - beállítja
     *   = a státuszt
     *     ÷ WAITING_FOR_DELIVERY-re, ha a szállítási mód DELIVERY_SERVICE
     *     × DELIVERED-re, ha a szállítási mód DIRECT_RECEIVING
     *   = a fizetés dátumát (paymentDate) a gépidőre
     *   = a fizetve (paid) mezőt true-ra
     */
    @Test
    @Order(2)
    void confirmPayment() {
        // a státusz: BOOKED
        assertEquals(OrderStatusOnlineEnum.BOOKED, order.getOrderStatus());
        // fizetés dátuma üres
        assertNull(order.getPaidDate());
        // fizetve false
        assertFalse(order.getPaid());
        order.confirmPayment();

        //...és az eredmények:
        // a státusz: WAITING_FOR_DELIVERY (szállítási mód "futárszolgálat" (DELIVERY_SERVICE) volt)
        assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order.getOrderStatus());
        // fizetés dátuma nem üres
        assertNotNull(order.getPaidDate());
        // LocalDateTime.now() kerül bele, ezt nem
        // vethetem össze a "mostani" LocalDateTime.now()-val (vagy igen?)
        // HÁT NEM!!! (néha jó, de inkább nem)
        //assertEquals(LocalDateTime.now(), order.getPaymentDate());
        // fizetve true
        assertTrue(order.getPaid());

        //-----------------------------------------------------------------------
        // ez eddig a prolog()-ban létrehozott "futárszolgálat"-os változat,
        // most csinálok egy másik, "személyes átvétel"-est.
        // Ilyenkor a fizetés megerősítése után a státusz egyből DELIVERED lesz.
        // (új kosár is kell, mert az előzőt levásároltuk)
        cart = new Cart();
        try {
            cart.addNewProduct(prod2.getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | CartClosedException e) {
            e.printStackTrace();
        }
        OrderOnline order1 = createOrder(cart);
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), DeliveryModeEnum.DIRECT_RECEIVING);
            order1.confirmPayment();
            // a státusz: DELIVERED (átvételi mód "személyes átvétel" (DIRECT_RECEIVING) volt)
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order1.getOrderStatus());
            // fizetés dátuma nem üres
            assertNotNull(order1.getPaidDate());
            // fizetve true
            assertTrue(order1.getPaid());
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException | NotEnoughItemException e) {
            e.printStackTrace();
        }
    }

    /**
     * passToDeliveryService() - átadás a futárszolgálatnak
     * - ha a státusz nem WAITING_FOR_DELIVERY, akkor InvalidOrderOperationException
     *   (miután WAITING_FOR_DELIVERY csak DELIVERY_SERVICE átvételi mód esetén fordulhat
     *    elő, ezt külön nem vizgálom a metódusban, tesztelni sem kell)
     * - ha a fizetési mód NEM "utánvét" (ADDITIONAL), ÉS
     *   még nincs fizetve, akkor InvalidOrderOperationException
     * - a státusz IN_PROGRESS lesz
     * - az átadva dátum (passToServiceDate) a gépidő lesz
     */
    @Test
    @Order(3)
    void passToDeliveryService() {
        String message = MESSAGE_DEFAULT;
        try {
            //if (order.getOrderStatus() == OrderStatusOnlineEnum.PENDING) dispatchOrder();
            // az előbb lefutott a fizetés megerősítése, a státusz WAITING_FOR_DELIVERY
            assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order.getOrderStatus());
            order.passToDeliveryService();
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertEquals(MESSAGE_DEFAULT, message);
        // a státusz: IN_PROGRESS
        assertEquals(OrderStatusOnlineEnum.IN_PROGRESS, order.getOrderStatus());
        // átadva dátuma nem üres
        assertNotNull(order.getPassedToServiceDate());
    }

    /**
     * confirmDelivery() - szállítás/átvétel megerősítése
     * - ha a státusz nem IN_PROGRESS, akkor az InvalidOrderOperationException
     *   (miután IN_PROGRESS csak DELIVERY_SERVICE átvételi mód esetén fordulhat
     *    elő, ezt külön nem vizgálom a metódusban, ezt tesztelni sem kell)
     * - ha nem sikeres a szállítás ÉS a sikertelenségi megjegyzés nincs kitöltve,
     *   akkor az InvalidOrderOperationException
     * - ha
     *   = sikeres, akkor
     *     ÷ a státusz DELIVERED,
     *     ÷ a fizetve tru lesz
     *   = nem sikeres, akkor
     *     ÷ a státusz FAILED_DELIVERY és
     *     × beállítjuk a failureComment-et
     * - kiszállítva dátum a gépidő lesz
     */
    @Test
    @Order(4)
    void confirmDelivery() {
        String message;
        try {
            assertEquals(OrderStatusOnlineEnum.IN_PROGRESS, order.getOrderStatus());
            order.confirmDelivery(true);
            message = MESSAGE_DEFAULT;
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertEquals(MESSAGE_DEFAULT, message);
        // a státusz: DELIVERED
        assertEquals(OrderStatusOnlineEnum.DELIVERED, order.getOrderStatus());
        // kiszállítva dátuma nem üres
        assertNotNull(order.getDeliveredDate());
    }

    // closeOrder() - rendelés lezárása
    // - ha a rendelés fizetése nincs rendezve, az InvalidOrderOperationException
    // - ha a rendelés nincs kiszállítva akár sikeresen (DELIVERED), akár sikertelenül (FAILED_DELIVERY),
    //   az InvalidOrderOperationException
    // - ha as kézbesítés
    //   = sikertelen, akkor felszabadítja az összes, a rendelésben lefoglalt terméket
    //   = sikeres, akkor véglegesíti a raktárkészletben a készletcsökkenést
    // - zárásdátumot beállítja a gépidőre.
    @Test
    @Order(5)
    void closeOrder() {
        // nézzük a "normális" menetet (feladva, fizetve, kiszállítva)
        try {
            // tehát a kiindulás:
            // - rendelés fizetve, (sikeresen) kiszállítva
            // - prod1, prod2, prod3 100-100 mennyiséggel van képviselve a rendelésben
            // - a raktárban prod1, prod2, prod3-ból 100, 500, 1000 van
            assertTrue(order.getPaid());
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order.getOrderStatus());
            assertEquals(100, order.productItems().get(0).getQuantity());
            assertEquals(100, order.productItems().get(1).getQuantity());
            assertEquals(100, order.productItems().get(2).getQuantity());
            assertEquals(100, stock.findItem(prod1.getItemNumber()).getQuantity());
            assertEquals(400, stock.findItem(prod2.getItemNumber()).getQuantity());
            assertEquals(1000, stock.findItem(prod3.getItemNumber()).getQuantity());
            order.closeOrder(stock);
            // a termékekből 0, 400 és 900 kell maradjon a raktárban 0, 0, 0 foglalással
            assertEquals(0, stock.productItemList().get(0).getQuantity());
            assertEquals(300, stock.productItemList().get(1).getQuantity());
            assertEquals(900, stock.productItemList().get(2).getQuantity());
            assertEquals(0, ((StockItem)stock.findItem(prod1.getItemNumber())).getBookedQuantity());
            assertEquals(0, ((StockItem)stock.findItem(prod2.getItemNumber())).getBookedQuantity());
            assertEquals(0, ((StockItem)stock.findItem(prod3.getItemNumber())).getBookedQuantity());
        } catch (InvalidOrderOperationException | InvalidQuantityArgumentException | NotEnoughItemException e) {
            e.printStackTrace();
        }
        // ez eddig "szabályos" folyamatot (feladás, fizetés, átadás futárnak,
        // szállítás/átvétel megerősítése, lezárás) követve teszteltem
        //-----------------------------------------------------------------------

        //-----------------------------------------------------------------------
        // most nézzük a kezelt hibákat:
        // - csinálok előbb egy "nem kifizetett"-et
        // (új kosár is kell, mert az előzőt "levásároltuk").
        Cart cart = new Cart();
        try {
            cart.addNewProduct(prod2.getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | CartClosedException e) {
            e.printStackTrace();
        }
        // ezen a ponton a raktárban van
        // - 0 db prod1,
        // - 400 db prod2, amiből 100 le van foglalva és
        // - 900 db prod3.
        OrderOnline order1 = createOrder(cart);
        assertEquals(100, order.productItems().get(1).getQuantity());
        String message = MESSAGE_DEFAULT;
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), order.getDeliveryMode());
            // a fizetés megerősítése kimarad
            //order1.confirmPayment();
            // tehát
            // - a fizetve (paid): false
            // - a státusz: BOOKED a feladás után
            assertFalse(order1.getPaid());
            assertEquals(OrderStatusOnlineEnum.BOOKED, order1.getOrderStatus());
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | NotEnoughItemException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        // "...nincs kiegyenlítve..." kell
        assertTrue(message.contains("Számla nincs kiegyenlítve, a rendelés nem zárható le"));

        // nézzük tovább a "fizetve" után
        try {
            // a fizetés megerősítése
            order1.confirmPayment();
            // tehát
            // - a fizetve (paid): true
            // - a státusz: WAITING_FOR_DELIVERY a fizetés után
            assertTrue(order1.getPaid());
            assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order1.getOrderStatus());
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // "Nem véglegesített..." kell
        assertTrue(message.contains("Nem véglegesített rendelés nem zárható le"));

        // ha ki is szállítom, akkor - "sikeres" átvétel esetén - le kell futnia
        // a closeOrder()-nek, ezért előbb kipróbálom a "sikertelen"-t a "megjegyzés"-sel.
        try {
            // a fizetés megerősítése
            order1.confirmPayment();
            // tehát
            // - a fizetve (paid): true
            assertTrue(order1.getPaid());
            // - a státusz: WAITING_FOR_DELIVERY a fizetés után
            assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order1.getOrderStatus());
            // adjuk át a futárnak a csomagot
            order1.passToDeliveryService();
            // - a státusz: IN_PROGRESS az átadás után
            assertEquals(OrderStatusOnlineEnum.IN_PROGRESS, order1.getOrderStatus());
            // kézbesítés: sikertelen és nincs "megjegyzés": InvalidOrderOperationException
            order1.confirmDelivery(false);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // "...a sikertelenség okát..." kell
        assertTrue(message.contains("Sikertelen átvétel esetén a sikertelenség okát fel kell tűntetni"));

        // A "sikertelen - megjegyzéssel" esetén viszont hiba nélkül le kell fusson
        message = MESSAGE_DEFAULT;
        try {
            // - a státusz: IN_PROGRESS kellene legyen
            assertEquals(OrderStatusOnlineEnum.IN_PROGRESS, order1.getOrderStatus());
            // a fizetés megerősítése nem fut hibára, de nem is csinál semmit
            LocalDateTime before = order1.getPaidDate();
            order1.confirmPayment();
            assertEquals(before, order1.getPaidDate());
            // kézbesítés: sikertelen
            order1.confirmDelivery(false, "Sérült csomagolás miatt nem vették át");
            // tehát
            // - a fizetve (paid): true
            // - a státusz: FAILED_DELIVERY a sikertelen kiszállítás után
            // - a prod2-ből van 100 foglalt
            assertTrue(order1.getPaid());
            assertEquals(OrderStatusOnlineEnum.FAILED_DELIVERY, order1.getOrderStatus());
            assertEquals(100, ((StockItem)stock.findItem(prod2.getItemNumber())).getBookedQuantity());
            order1.closeOrder(stock);
            // prod2 100 foglalása felszabadítva: 0 foglalt
            assertEquals(0, ((StockItem)stock.findItem(prod2.getItemNumber())).getBookedQuantity());
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // MESSAGE_DEFAULT kell
        assertEquals(MESSAGE_DEFAULT, message);
        // a sikeres végigfutását már láttam az első tesztesetben, itt nem kívánom újra
        //-----------------------------------------------------------------------

        //-----------------------------------------------------------------------
        // - csinálok még egy ""személyes átvétel"-est is: ez abban különbözik, hogy a
        //   fizetés megerősítése után egyből DELIVERED lesz
        //
        // (itt ismét kell egy új kosár, mert az előzőt is levásároltuk - bár sikertelenül
        cart = new Cart();
        try {
            // foglaljunk most a prod3-ból 100-at
            cart.addNewProduct(prod3.getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | CartClosedException e) {
            e.printStackTrace();
        }
        order1 = createOrder(cart);
        // a prod3-ból van 900 raktáron, amiből 100 foglalt
        message = MESSAGE_DEFAULT;
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), DeliveryModeEnum.DIRECT_RECEIVING);
            // a fizetés megerősítése
            order1.confirmPayment();
            // tehát
            // - a fizetve (paid): true
            // - a státusz: DELIVERED a fizetés megerősítése után
            assertTrue(order1.getPaid());
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order1.getOrderStatus());
            // tehát prod3-ból 900 van a raktárban, 100 foglalt (ezt most majd levesszük)
            assertEquals(900, stock.findItem(prod3.getItemNumber()).getQuantity());
            assertEquals(100, ((StockItem)stock.findItem(prod3.getItemNumber())).getBookedQuantity());
            order1.closeOrder(stock);
            // prod2-ből 300 maradt a raktárban, 0 foglalt
            assertEquals(800, stock.findItem(prod3.getItemNumber()).getQuantity());
            assertEquals(0, ((StockItem)stock.findItem(prod3.getItemNumber())).getBookedQuantity());
        } catch (InvalidOrderOperationException | NotEnoughItemException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        // Nincs hiba, minden OK
        assertEquals(MESSAGE_DEFAULT, message);

    }

/*

    megszűntettem a setCustomer()-t
    @Test
    void setCustomer() {
        assertEquals(1L, order.getCustomer().getCustomerID());

        // csinálok egy másik vevőt, amit beállítok az Order-ben
        final Long customerID = 2L;
        final String name = "Másik vevő";
        final String address = "Cím2";
        final String phoneNumber = "ph.num.2";
        final String email = "lofarok@g.hu";
        final String deliveryAddress = "ide a csomagot";
        final String accountAddress = "ide a számlát";

        // ezzel a konstructorral az accountAddress megkapja a deliveryAddress értékét
        order.setCustomer(new Customer(customerID, name, address, phoneNumber,email,deliveryAddress));
        // mostantól a 2-es ID-jű vevőé az Order
        assertEquals(2L, order.getCustomer().getCustomerID());
        assertEquals(name, order.getCustomer().getName());
        assertEquals(address, order.getCustomer().getAddress());
        assertEquals(phoneNumber, order.getCustomer().getPhoneNumber());
        assertEquals(email, order.getCustomer().getEmail());
        // tehát accountAddress ugyanaz, mint deliveryAddress
        assertEquals(deliveryAddress, order.getCustomer().getDeliveryAddress());
        assertEquals(deliveryAddress, order.getCustomer().getAccountAddress());

        // "teljes" konstruktorral - nyilván - más-más értéke lesz mindkettőnek
        order.setCustomer(new Customer(customerID, name, address, phoneNumber,email,deliveryAddress, accountAddress));
        assertEquals(deliveryAddress, order.getCustomer().getDeliveryAddress());
        assertEquals(accountAddress, order.getCustomer().getAccountAddress());
    }

*/
}