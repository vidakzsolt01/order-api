package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
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

// Az egyes metódusok tesztjeit a megrendelés tervezett életciklusa szerinti
// sorrendben akaraom futtatni, hogy lássam, hogy ha minden "szabályosan" halad,
// akkor mit mutat a kód.
// A "szabályos" sorrend (egy online, bakkártyás, futárszolgálatos rendelés esetén)
// tehát:
// - dispatchOrder()         - rendelés feladása
// - confirmPayment()        - fizetés megerősítése
// - passToDeliveryService() - átadás a futárszolgálatnak
// - confirmDelivery()       - szállítás/átvétel megerősítése.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderOnlineTest__ extends Container {

    static final String MESSAGE_DEFAULT = "no message";
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
        // ezen a szinte az Exception-ökkel nem foglalkozom...
        //--------------------------------------------------------------------------------
        stock = new Stock();
        try {
            stock.depositProduct(new Product("111111", "Termék-1", 1000, 27), 100);
            stock.depositProduct(new Product("222222", "Termék-2", 2000, 5), 500);
            stock.depositProduct(new Product("333333", "Termék-3", 2000, 5), 1000);
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // Megvan a Raktár, most feltöltök Kosarat a rakétártételekből
        List<ProductItem> stockProduts = stock.productItemList();
        cart = new Cart();
        try {
            cart.addNewProduct(stockProduts.get(0).getProduct().getItemNumber(), 100, stock);
            cart.addNewProduct(stockProduts.get(1).getProduct().getItemNumber(), 100, stock);
            cart.addNewProduct(stockProduts.get(2).getProduct().getItemNumber(), 100, stock);
        } catch (NotEnoughItemException | InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }

        // Végül itt a kezdeti végcél: csinálok egy OrderOnline-t a Cart.closeCart()-tal
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException e) {
            e.printStackTrace();
        }
    }

    // az egyes tesztmetódusok hibatesztjeihez időnként kell csinálnom egy-egy
    // új Order-t, ehhez csinálok egy OrderOnline csináló metódust
    private OrderOnline createOrder(){
        OrderOnline order = null;
        try {
            order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
        } catch (CartIsEmptyException e) {
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

        // customer-adatok rendben, szállítási mód rendben, fizetési mód
        // nincs átadva: InvalidOrderOperationException
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
            // fizetési mód: bankkártya, átvételi mód: futárszolgálat
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
        assertNull(order.getPayedDate());
        // fizetve false
        assertFalse(order.getPaid());
        order.confirmPayment();

        //...és az eredmények:
        // a státusz: WAITING_FOR_DELIVERY (szállítási mód "futárszolgálat" (DELIVERY_SERVICE) volt)
        assertEquals(OrderStatusOnlineEnum.WAITING_FOR_DELIVERY, order.getOrderStatus());
        // fizetés dátuma nem üres
        assertNotNull(order.getPayedDate());
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
        OrderOnline order1 = createOrder();
        try {
            //kell futtatni egy feladást a BOOKED státusz miatt
            order1.dispatchOrder(order.getCustomer(), order.getPaymentMode(), DeliveryModeEnum.DIRECT_RECEIVING);
            order1.confirmPayment();
            // a státusz: DELIVERED (átvételi mód "személyes átvétel" (DIRECT_RECEIVING) volt)
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
     * passToDeliveryService() - átadás a futárszolgálatnak
     * - ha a státusz nem WAITING_FOR_DELIVERY, akkor InvalidOrderOperationException
     * - ha a fizetési mód NEM "utánévét" (ADDITIONAL), ÉS
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

    @Test
    @Order(5)
    void closeOrder() {
    }

    @Test
    void setPaymentMode() {
    }

    @Test
    void setDeliveryMode() {
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
        final String deliveryAddress = "ideacsomagot";
        final String accountAddress = "ideaszámlát";

        // ezzel a constructorral az accountAddress megkapja a deliveryAddress értékét
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