package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.StockItem;
import org.junit.jupiter.api.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

// Az egyes metódusok tesztjeit a megrendelés tervezett életciklusa szerinti
// sorrendben akaraom futtatni (egyik metódus által állított értékeket
// felhasználom a következőben), hogy lássam, hogy ha minden "szabályosan" halad,
// akkor mit mutat a kód.
// A "szabályos" sorrend (egy közvetlen (személyes) vásárlás (rendelés) esetén)
// tehát:
// - dispatchOrder()         - rendelés feladása
// - confirmPayment()        - fizetés megerősítése
// - closeOrder()            - rendelés lezárása.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderDirectTest extends Container {

    // controll message a hibaüzenetek (exceptions) ellenőrzéséhez
    static final String MESSAGE_DEFAULT = "no message";

    // Alapvetés: kell pár Termék
    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 5);

    // az egyszerűség kedvéért általában a tesztek globális alapobjektumokkal
    // operálnak, a speciális esetekhez maj "helybe" deklarálom a szükségeseket
    static OrderDirect order;
    static Customer customer = null;
    static Stock stock;
    static Cart cart;

    @BeforeAll
    static void prolog() {
        //--------------------------------------------------------------------------------
        // A tesztekhez kell egy OrderDirect object, amit a Cart.closeCart()-ja hoz létre,
        // tehát kell  egy Cart.
        // A Cart feltöltését viszont raktárból lehet intézni, tehát kell egy
        // Raktár (Stock) is, kezdem ezzel
        //-----------------------------------------------
        // itt nem a raktárat és a kosarat akarom tesztelni: az exception-ökkel nem foglalkozom...
        //--------------------------------------------------------------------------------
        stock = Stock.getInstance();
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

        // Végül itt a kezdeti végcél: csinálok egy OrderDirect-t a Cart.closeCart()-tal
        try {
            order = (OrderDirect) cart.closeCart(ShoppingModeEnum.DIRECT);
        } catch (CartIsEmptyException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    // az egyes tesztmetódusok hibatesztjeihez időnként kell csinálnom egy-egy
    // új Order-t, ehhez csinálok egy OrderDirect csináló metódust, mely
    // "aktuális" kosártartalomból tud készülni
    private OrderDirect createOrder(Cart cart){
        OrderDirect order = null;
        try {
            order = (OrderDirect) cart.closeCart(ShoppingModeEnum.DIRECT);
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
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Vásárló-adatok nélkül"));

        // hiányos customer-adatok (nincs cím, email és telefon): InvalidOrderOperationException
        customer = new Customer(1L, "Vevő");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // hiányos customer-adatok (nincs email és telefon): InvalidOrderOperationException
        customer.setDeliveryAddress("Cím");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // hiányos customer-adatok (telefon): InvalidOrderOperationException
        customer.setEmail("email@gcim.hu");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Kötelező vásárló-adatok hiányoznak"));

        // customer-adatok rendben, fizetési mód hibás (csak CASH, v. CREDIT_CARD lehet): InvalidOrderOperationException
        customer.setPhoneNumber("phonenumber");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.ADDITIONAL);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Ez a fizetési mód ennél a megrendeléstípusnál nem választható"));

        // customer-adatok rendben, szállítási mód rendben, fizetési rendben, elvileg feladható
        // (a feladáshoz a státusznak PENDING-nek kell lennie, de a státusz "kívülről" nem
        //  állítható közvetlenül, ezért ez nem tesztelhető innen; egyébként az Order-t kapcsiból
        //  PENDING státusszal példányosítom, úgyhogy az nem lehet rossz), de azért...
        // ezt ellenőrizhetem:
        assertEquals(OrderStatusDirectEnum.PENDING, order.getOrderStatus());

        try {
            // fizetési mód: bankkártya
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD);
            message = MESSAGE_DEFAULT;
        } catch (InvalidOrderOperationException | InvalidPaymentModeException e) {
            message = e.getMessage();
        }
        // nem volt hiba?
        assertEquals(MESSAGE_DEFAULT, message);
        // státusz beállt DELIVERED-re?
        assertEquals(OrderStatusDirectEnum.BOOKED, order.getOrderStatus());
    }

    /**
     * confirmPayment() - fizetés megerősítése
     * - ha BOOKED a státusz (státuszt nem lehet közvetlenül állítani,
     *   ezt nem tudom tesztelni)
     * - beállítja
     *   = a státuszt DELIVERED-re
     *   = a fizetve (paid) mezőt true-ra
     *   = a fizetés dátumát (paymentDate) a gépidőre
     *   = az átvétel dátumát (deliveredDate) a gépidőre
     */
    @Test
    @Order(2)
    void confirmPayment() {
        String message = MESSAGE_DEFAULT;
        // induló állapot:
        // - fizetve = false
        // - fizetve dátum üres
        // - átvéve dátum üres
        // - státusz = BOOKED
        assertFalse(order.getPaid());
        assertNull(order.getPaidDate());
        assertNull(order.getDeliveredDate());
        assertEquals(OrderStatusDirectEnum.BOOKED, order.orderStatus);
        try {
            order.confirmPayment();
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // nem lehet hiba
        assertEquals(MESSAGE_DEFAULT, message);
        // végállapot:
        // - fizetve = true
        // - fizetve dátum nem üres
        // - átvéve dátum nem üres
        // - státusz = DELIVERED
        assertTrue(order.getPaid());
        assertNotNull(order.getPaidDate());
        assertNotNull(order.getDeliveredDate());
        assertEquals(OrderStatusDirectEnum.DELIVERED, order.orderStatus);
    }

    /**
     * Ha a státusz DELIVERED, akkor
     * - véglegesíti a raktárkészletben a rendelés tételeit
     * - zárás dátuma (closedDate) a gépidő lesz
     */
    @Test
    @Order(3)
    void closeOrder() {
        String message = MESSAGE_DEFAULT;
        // induló állapot
        // - zárás dátum üres
        // - státusz: DELIVERED
        // - a raktárban 100 prod1-ből 100, az 500 prod2-ből 100 és az 1000 prod2-ből 100 foglalt,
        assertNull(order.getClosedDate());
        assertEquals(OrderStatusDirectEnum.DELIVERED, order.orderStatus);
        assertEquals(100, stock.findItem(prod1.getItemNumber()).getQuantity());
        assertEquals(500, stock.findItem(prod2.getItemNumber()).getQuantity());
        assertEquals(1000, stock.findItem(prod3.getItemNumber()).getQuantity());
        assertEquals(100, ((StockItem)stock.findItem(prod1.getItemNumber())).getBookedQuantity());
        assertEquals(100, ((StockItem)stock.findItem(prod2.getItemNumber())).getBookedQuantity());
        assertEquals(100, ((StockItem)stock.findItem(prod3.getItemNumber())).getBookedQuantity());
        try {
            // a bemeneti státusz rendben, a raktárkészletben megvan minden, nem számítok hibára
            order.closeOrder(stock);
        } catch (NotEnoughItemException | InvalidOrderOperationException e) {
            e.printStackTrace();
        }
        // végállapot
        // - zárás dátum nem üres
        // - státusz: CLOSED marad
        // - a raktárban 0 prod1-ből 0, a 400 prod2-ből 0 és az 900 prod2-ből 0 foglalt,
        assertNotNull(order.getClosedDate());
        assertEquals(OrderStatusDirectEnum.CLOSED, order.orderStatus);
        assertEquals(0, stock.findItem(prod1.getItemNumber()).getQuantity());
        assertEquals(400, stock.findItem(prod2.getItemNumber()).getQuantity());
        assertEquals(900, stock.findItem(prod3.getItemNumber()).getQuantity());
        assertEquals(0, ((StockItem)stock.findItem(prod1.getItemNumber())).getBookedQuantity());
        assertEquals(0, ((StockItem)stock.findItem(prod2.getItemNumber())).getBookedQuantity());
        assertEquals(0, ((StockItem)stock.findItem(prod3.getItemNumber())).getBookedQuantity());

        //-----------------------------------------------------------------------------
        // eddig volt a "sima" menet, lássunk hibásat is
        // pl. "idő előtti" zárás
        // Csinálok egy rendelést, amelyhez új kosár is kell
        cart = new Cart();
        try {
            cart.addNewProduct(prod2.getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | CartClosedException e) {
            e.printStackTrace();
        }
        OrderDirect order1 = createOrder(cart);
        //ha most akarok zárni a "szűz" rendelésre, akkor InvalidOrderOperationException ()
        try {
            // a státusza még várakozó
            assertEquals(OrderStatusDirectEnum.PENDING, order1.getOrderStatus());
            order1.closeOrder(stock);
        } catch (NotEnoughItemException | InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // "Nem véglegesített rendelés nem zárható le."
        assertTrue(message.contains("Nem véglegesített rendelés nem zárható le."));

        // ha a fizetést akarom megerősíteni
        try {
            // a státusza még mindig várakozó
            assertEquals(OrderStatusDirectEnum.PENDING, order1.getOrderStatus());
            order1.confirmPayment();
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // "A rendelés még nem került feladásra."
        assertTrue(message.contains("A rendelés még nem került feladásra."));

        // akkor most feladom a megrendelést
        try {
            // a státusza még várakozó
            assertEquals(OrderStatusDirectEnum.PENDING, order1.getOrderStatus());
            order1.dispatchOrder(customer, PaymentModeEnum.CASH);
            // a státusza foglalt
            assertEquals(OrderStatusDirectEnum.BOOKED, order1.getOrderStatus());
            //ha most akarok zárni, akkor még mindig InvalidOrderOperationException
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | InvalidPaymentModeException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // "Nem véglegesített rendelés nem zárható le."
        assertTrue(message.contains("Nem véglegesített rendelés nem zárható le."));

        // ha most akarom kifizetni
        try {
            // a státusza foglalt
            assertEquals(OrderStatusDirectEnum.BOOKED, order1.getOrderStatus());
            order1.confirmPayment();
            // akkor annak menni kell, státusz: DELIVERED
            assertEquals(OrderStatusDirectEnum.DELIVERED, order1.getOrderStatus());
            // és ha most lezárom, akkor az mennie kell
            order1.closeOrder(stock);
            message = MESSAGE_DEFAULT;
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // minden OK
        assertEquals(MESSAGE_DEFAULT, message);

    }

    @Test
    void validatePaymentModeToSet() {
    }

}