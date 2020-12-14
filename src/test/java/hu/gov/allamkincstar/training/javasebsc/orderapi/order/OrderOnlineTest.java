package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.DeliveryModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusOnlineEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.CartIsEmptyException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderOnlineTest extends Container {

    static final String MESSAGE_DEFAULT = "no message";
    static OrderOnline order;
    static Customer customer = null;
    static Stock stock;
    static Cart cart;

    @BeforeAll
    static void prolog() {
        // a tesztekhez kellene egy OrderOnline, amit a Cart.closeCart()-ja hoz létre,
        // tehát csinálok egy Cart-ot, aminek a feltöltését viszont raktárból
        // lehet intézni, tehát kezdem a raktárral
        stock = new Stock();
        try {
            stock.depositProduct(new Product("111111", "Termék-1", 1000, 27), 100);
            stock.depositProduct(new Product("222222", "Termék-2", 2000, 5), 500);
            stock.depositProduct(new Product("333333", "Termék-3", 2000, 5), 1000);
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // tehát csinálok Cart-ot a raktártételekből
        List<ProductItem> stockProducts = stock.productItemList();
        cart = new Cart();
        try {
            cart.addNewProduct(stockProducts.get(0).getProduct().getItemNumber(), 100, stock);
            cart.addNewProduct(stockProducts.get(1).getProduct().getItemNumber(), 100, stock);
            cart.addNewProduct(stockProducts.get(2).getProduct().getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // és itt a kezdeti végcél: csinálok OrderOnline-t
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException e) {
            e.printStackTrace();
        }
    }

    private OrderOnline createOrder(Cart cart){
        OrderOnline order = null;
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException e) {
            e.printStackTrace();
        }
        return order;
    }

    /**
     * A rendelésfeladás akkor lehetséges, ha
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
        // miután az Order épp most jött létre, a rendelésfeladás nem mehet,
        // InvalidOrderOperationException-hoz vezet
        String message = MESSAGE_DEFAULT;

        // customer kezdetben null: InvalidOrderOperationException
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("ásárló-adatok"));

        // hiányos customer-adatok (nincs cím, email és telefon): InvalidOrderOperationException
        customer = new Customer(1L, "Vevő");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("ásárló-adatok"));

        // hiányos customer-adatok (nincs email és telefon): InvalidOrderOperationException
        customer.setDeliveryAddress("Cím");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("ásárló-adatok"));

        // hiányos customer-adatok (telefon): InvalidOrderOperationException
        customer.setEmail("email@gcim.hu");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("ásárló-adatok"));

        // customer-adatok rendben, szállítási mód nincs átadva: InvalidOrderOperationException
        customer.setPhoneNumber("phonenumber");
        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, null);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Nem választott szállítási módot"));

        // customer-adatok rendben, szállítási mód rendben, fizetési nincs átadva: InvalidOrderOperationException
        try {
            order.dispatchOrder(customer, null, DeliveryModeEnum.DELIVERY_SERVICE);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        assertTrue(message.contains("Nem választott fizetési módot"));

        // customer-adatok rendben, szállítási mód rendben, fizetési rendben, elvileg feladható
        // (a feladáshoz a státusznak PENDING-nek kell lennie, de a státusz "kívülről" nem
        //  állítható közvetlenül, ezért ez nem tesztelhető innen; egyébként az Order-t kapcsiból
        //  PENDING státusszal példányosítom, úgyhogy az nem lehet rossz), de azért...
        // ezt ellenőrizhetem:
        assertEquals(OrderStatusOnlineEnum.PENDING, order.getOrderStatus());

        try {
            order.dispatchOrder(customer, PaymentModeEnum.CREDIT_CARD, DeliveryModeEnum.DELIVERY_SERVICE);
            message = MESSAGE_DEFAULT;
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // nem volt hiba?
        assertEquals(MESSAGE_DEFAULT, message);
        // státusz beállt BOOKED-ra?
        assertEquals(OrderStatusOnlineEnum.BOOKED, order.getOrderStatus());
    }

    /**
     * confirmPayment()
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
        assertNull(order.getPayedDate());
        // fizetve false
        assertFalse(order.getPaid());
        order.confirmPayment();

        //...és az eredmények:
        // a státusz: WAITING_FOR_DELIVERY (szállítási mód "futárszolgálat" (DELIVERY_SERVICE) volt)
        assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order.getOrderStatus());
        // fizetés dátuma nem üres
        assertNotNull(order.getPayedDate());
        // LocalDateTime.now() kerül bele, ezt nem vethetem össze
        // a "mostani" LocalDateTime.now()-val (vagy igen?)
        // (HÁT NEM!!!)
        //assertEquals(LocalDateTime.now(), order.getPaymentDate());
        // fizetve true
        assertTrue(order.getPaid());

        //-----------------------------------------------------------------------
        // ez eddig a prolog()-ban létrehozott "futárszolgálat"-os változat,
        // most csinálok egy "személyes átvétel"-est. Ilyenkor a státusz DELIVERED lesz.
        OrderOnline order1 = createOrder(cart);
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), DeliveryModeEnum.DIRECT_RECEIVING);
            order1.confirmPayment();
            // a státusz: WAITING_FOR_DELIVERY (szállítási mód "futárszolgálat" (DELIVERY_SERVICE) volt)
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order1.getOrderStatus());
            // fizetés dátuma nem üres
            assertNotNull(order1.getPayedDate());
            // fizetve true
            assertTrue(order1.getPaid());
        } catch (InvalidOrderOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * passToDeliveryService()
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
        String message;
        try {
            if (order.getOrderStatus() == OrderStatusOnlineEnum.PENDING) dispatchOrder();
            assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order.getOrderStatus());
            order.passToDeliveryService();
            message = MESSAGE_DEFAULT;
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
     * confirmDelivery()
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
            // - a raktárban prod1, prod2, prod3-ból 1000, 2000, 2000 van
            assertTrue(order.getPaid());
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order.getOrderStatus());
            assertEquals(100, order.productItems().get(0).getQuantity());
            assertEquals(100, order.productItems().get(1).getQuantity());
            assertEquals(100, order.productItems().get(2).getQuantity());
            assertEquals(100, stock.productItemList().get(0).getQuantity());
            assertEquals(500, stock.productItemList().get(1).getQuantity());
            assertEquals(1000, stock.productItemList().get(2).getQuantity());
            order.closeOrder(stock);
            // a termékekből 0, 400 és 900 kell maradjon a raktárban
            assertEquals(0, stock.productItemList().get(0).getQuantity());
            assertEquals(400, stock.productItemList().get(1).getQuantity());
            assertEquals(900, stock.productItemList().get(2).getQuantity());
        } catch (InvalidOrderOperationException | InvalidQuantityArgumentException | NotEnoughItemException e) {
            e.printStackTrace();
        }
        // ez eddig "szabályos" folyamatot (feladás, fizetés, átadás futárnak, szállítás
        // megerősítése, lezárás) követve teszteltem, most nézzük a kezelt
        //-----------------------------------------------------------------------

        //-----------------------------------------------------------------------
        // most nézzük a kezelt hibákat:
        // - csinálok előbb egy "nem kifizetett"-et
        // (új kosár is kell, mert az előzőt "levásároltuk").
        Cart cart = new Cart();
        try {
            cart.addNewProduct(stock.productItemList().get(1).getIndex(), 100, stock);
        } catch (NotEnoughItemException e) {
            e.printStackTrace();
        }
        OrderOnline order1 = createOrder(cart);
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
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
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

        // ha ki is szállítom, akkor le kell futnia a closeOrder()-nek, de miután
        // ebben a rednelésben is van 100 prod1 (emlékeztető: az eredeti
        // kosárból (lásd: prolog()) csináltam ezt is), ÉS ezt a 100-at a
        // szabályos menetben már kivettem a raktárból, itt NotEnoughItemException
        // várható - ha "sikeres"-nek mondom a szállítást. "Sikertelen" esetben
        // szintén NotEnoughItemException, épp csak "...nincs foglalva a felszabadításhoz..."
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
            // kézbesítés: sikeres
            order1.confirmDelivery(true);
            // - a státusz: DELIVERED a megerősítés után
            assertEquals(OrderStatusOnlineEnum.DELIVERED, order1.getOrderStatus());
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // "...nincs lefoglalva..." kell
        assertTrue(message.contains("A véglegesíteni kívánt mennyiség nincs lefoglalva"));

        // A "sikertelen" esetén viszont hiba nélkül le kell fusson, DE...
        // előbb lássuk: a sikertelenségi megjegyzést nem adom meg
        // (új order1 kell, mert az előzőt ellőttem a DELIVERED-ig)
        order1 = createOrder(cart);
        message = MESSAGE_DEFAULT;
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), order.getDeliveryMode());
            // a fizetés megerősítése
            order1.confirmPayment();
            // - a státusz: WAITING_FOR_DELIVERY a fizetés után
            assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order1.getOrderStatus());
            // adjuk át a futárnak a csomagot
            order1.passToDeliveryService();
            // - a státusz: IN_PROGRESS az átadás után
            assertEquals(OrderStatusOnlineEnum.IN_PROGRESS, order1.getOrderStatus());
            // kézbesítés: sikertelen
            order1.confirmDelivery(false);
        } catch (InvalidOrderOperationException e) {
            message = e.getMessage();
        }
        // "Sikertelen átvétel... oka..." kell
        assertTrue(message.contains("Sikertelen átvétel esetén a sikertelenség okát fel kell tűntetni"));

        message = MESSAGE_DEFAULT;
        try {
            // a fizetés megerősítése
            order1.confirmPayment();
            // kézbesítés: sikertelen
            order1.confirmDelivery(false, "Sérült csomagolás miatt nem vették át");
            // tehát
            // - a fizetve (paid): true
            // - a státusz: FAILED_DELIVERY a sikertelen kiszállítás után
            assertTrue(order1.getPaid());
            assertEquals(OrderStatusOnlineEnum.FAILED_DELIVERY, order1.getOrderStatus());
            order1.closeOrder(stock);
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
        // MESSAGE_DEFAULT kell
        assertEquals(MESSAGE_DEFAULT, message);

/*
        //-----------------------------------------------------------------------
        // - csinálok még egy ""személyes átvétel"-est is
        //   (ez abban különbözik, hogy a fizetés megerősítése után egyből DELIVERED
        //    lesz).
        order1 = createOrder(cart);
        message = MESSAGE_DEFAULT;
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
        } catch (InvalidOrderOperationException | NotEnoughItemException e) {
            message = e.getMessage();
        }
*/
    }

    @Test
    void setCustomer() {
        assertEquals(1L, order.getCustomer().getCustomerID());
        /*
        Customer savedCustomer = new Customer(
                order.getCustomer().getCustomerID(),
                order.getCustomer().getName(),
                order.getCustomer().getAddress(),
                order.getCustomer().getPhoneNumber(),
                order.getCustomer().getEmail(),
                order.getCustomer().getDeliveryAddress(),
                order.getCustomer().getAccountAddress());
        */
        final Long customerID = 2L;
        final String name = "Másik vevő";
        final String address = "Cím2";
        final String phoneNumber = "ph.num.2";
        final String email = "lofarok@g.hu";
        final String deliveryAddress = "ide a csomagot";
        final String accountAddress = "ide a számlát";

        // ezzel a konstructorral az accountAddress megkapja a deliveryAddress értékét
        order.setCustomer(new Customer(customerID, name, address, phoneNumber,email,deliveryAddress));
        assertEquals(customerID, order.getCustomer().getCustomerID());
        assertEquals(name, order.getCustomer().getName());
        assertEquals(address, order.getCustomer().getAddress());
        assertEquals(phoneNumber, order.getCustomer().getPhoneNumber());
        assertEquals(email, order.getCustomer().getEmail());
        // tehát accountAddress ugyanaz, mint deliveryAddress
        assertEquals(deliveryAddress, order.getCustomer().getDeliveryAddress());
        assertEquals(deliveryAddress, order.getCustomer().getAccountAddress());

        // így más-más értéke lesz mindkettőnek
        order.setCustomer(new Customer(customerID, name, address, phoneNumber,email,deliveryAddress, accountAddress));
        assertEquals(deliveryAddress, order.getCustomer().getDeliveryAddress());
        assertEquals(accountAddress, order.getCustomer().getAccountAddress());
    }

}