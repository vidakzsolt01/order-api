package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest extends Container {

    // controll message a hibaüzenetek (exceptions) ellenőrzéséhez
    static final String MESSAGE_DEFAULT = "no message";

    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 12);
    static Product prod4 = new Product("444444", "Termék-4", 10, 27);
    static Stock stock = Stock.getInstance();

    @BeforeAll
    static void prolog(){
        for (ProductItem item:stock.productItemList()){
            stock.removeItem(item.getIndex());
        }
        try {
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 20);
            stock.depositProduct(prod3, 30);
            stock.depositProduct(prod4, 40);
        } catch (InvalidQuantityArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Azt akarom igazolni, hogy a Cart-ból megszerzett terméklista
     * nem módosítható:
     * <ul>
     *     <li>kinyerem a listát, </li>
     *     <li>hozzáadok egy elemet, majd</li>
     * </ul>
     * újra kinyerve a listát, az elemszám nem változhatott
     * <ul>
     *     <li>kinyerem a listát, </li>
     *     <li>módosítok egy elemet (0.), majd</li>
     * </ul>
     * újra kinyerve a listát a 0. elem nem változhatott
     */
    @Test
    void productItemList(){

        Cart cart = new Cart();

        // 0 termék van a listában
        assertEquals(0, cart.productItemList().size());
        try {
            // hozzáadunk 5 prod1-et
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            // 1 termék van a listában
            assertEquals(1, cart.productItemList().size());

            // hozzáadunk 10 prod2-t
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // 2 termék van a listában
            assertEquals(2, cart.productItemList().size());

            // hozzáadok egy elemet a lekért listához
            cart.productItemList().add(new OrderItem(prod1, 10));
            // kosárban továbbra is 2 elem van a listában
            assertEquals(2, cart.productItemList().size());

            // az 0. termékből 5 van a kosárlistában
            assertEquals(5, cart.productItemList().get(0).getQuantity());

            List<ProductItem> cartProducts = cart.productItemList();
            // megnövelem a lekért listában a 0. termék mennyiségét
            cartProducts.get(0).increaseQuantity(10);
            // a kosárban még mindig 5 van a 0.-ból
            assertEquals(5, cart.productItemList().get(0).getQuantity());

        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tesztelni akarom, hogy
     * <ul>
     *     <li>működik a termékhozzáadás</li>
     *     <li>ha ugyanazt terméket adom többször hozzá, akkor az elemszám nem,
     *     csak a, mennyiség növekszik</li>
     * </ul>
     */
    @Test
    void testAddNewProduct(){
        Cart cart = new Cart();

        try {
            // fogalmam sincs, mi van a raktárban az előző tesztek után,
            // adok hozzá pár prod1-et és prod2-t
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 10);
            // foglalok a kosárnak 5 prod1-et
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            // a kosárban 1 elem kell legyen
            assertEquals(1, cart.productItemList().size());
            // kérek 10 prod2-t is
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // a kosárban 2 elem kell legyen, ...
            assertEquals(2, cart.productItemList().size());
            // ... és pontosan 5 prod1, és 10 prod2
            assertEquals(5, cart.productItemList().get(0).getQuantity());
            assertEquals(10, cart.productItemList().get(1).getQuantity());
            // bepakolok további 5 prod1-et
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }
        // a kosárban továbbra is 2 termék kell, legyen, ...
        assertEquals(2, cart.productItemList().size());
        // ... és pontosan 10 prod1, és 10 prod2
        assertEquals(10, cart.productItemList().get(0).getQuantity());
        assertEquals(10, cart.productItemList().get(1).getQuantity());
    }

    /**
     * Azt akarom igazolni, hogy működik a termék eltávolítása
     */
    @Test
    void removeProduct(){
        Cart cart = new Cart();

        try {
            // fogalmam sincs, mi van a raktárban, az előző tesztek után,
            // adok hozzá pár prod1-et és prod2-t
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 10);
            // hozzáadok a kosárhoz pár terméket
            cart.addNewProduct(prod1.getItemNumber(), 10, stock);
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // a kosárban 2 termék van
            assertEquals(2, cart.productItemList().size());

            // visszateszem a polcra a prod1-eket
            cart.removeProduct(prod1.getItemNumber(), stock);
            // a kosárban 1 termék van
            assertEquals(1, cart.productItemList().size());

            // mi van, ha a prod1-eket ismét kiveszem? RunTimeException kellene, hogy legyen
            Exception ex = assertThrows(NoItemFoundException.class, () ->cart.removeProduct(prod1.getItemNumber(), stock));
            String message = "A keresett termék nem található.";
            String realMessage = ex.getMessage();
            assertEquals(realMessage, message);

            // visszateszem a polcra a prod2-eket is
            cart.removeProduct(prod2.getItemNumber(), stock);
            // a kosárban nem lehet semmi
            assertEquals(0, cart.productItemList().size());

        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Azt tesztelem, hogy működik a termékmennyiség növelése
     */
    @Test
    void testIncreaseItemQuantity(){
        Cart cart = new Cart();
        final int INDEX_PROD1 = 0;
        final int INDEX_PROD2 = 1;
        try {
            // fogalmam sincs, mi van a raktárban, az előző tesztek után,
            // adok hozzá pár prod1-et és prod2-t
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 10);
            // hozzáadok a kosárhoz pár terméket
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // a kosárban 2 termék van
            assertEquals(2, cart.productItemList().size());
            // ... és pontosan 5 prod1, és 10 prod2
            assertEquals(5, cart.productItemList().get(INDEX_PROD1).getQuantity());
            assertEquals(10, cart.productItemList().get(INDEX_PROD2).getQuantity());

            // növelem a prod1-et 3×, a prod2-t 2×
            cart.increaseItemQuantity(prod1.getItemNumber(), stock);
            cart.increaseItemQuantity(prod1.getItemNumber(), stock);
            cart.increaseItemQuantity(prod1.getItemNumber(), stock);
            cart.increaseItemQuantity(prod2.getItemNumber(), stock);
            cart.increaseItemQuantity(prod2.getItemNumber(), stock);
            // pontosan 8 prod1, és 12 prod2 kell, legyen
            assertEquals(8, cart.productItemList().get(INDEX_PROD1).getQuantity());
            assertEquals(12, cart.productItemList().get(INDEX_PROD2).getQuantity());

        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDecreaseItemQuantity(){
        Cart cart = new Cart();
        final int INDEX_PROD1 = 0;
        final int INDEX_PROD2 = 1;
        try {
            // fogalmam sincs, mi van a raktárban, az előző tesztek után,
            // adok hozzá pár prod1-et és prod2-t
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 10);
            // hozzáadok a kosárhoz pár terméket
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // a kosárban 2 termék van
            assertEquals(2, cart.productItemList().size());
            // ... és pontosan 5 prod1, és 10 prod2
            assertEquals(5, cart.productItemList().get(INDEX_PROD1).getQuantity());
            assertEquals(10, cart.productItemList().get(INDEX_PROD2).getQuantity());

            //csökkentem a prod1-et 3×, a prod2-t 2×
            cart.decreaseItemQuantity(prod1.getItemNumber(), stock);
            cart.decreaseItemQuantity(prod1.getItemNumber(), stock);
            cart.decreaseItemQuantity(prod1.getItemNumber(), stock);
            cart.decreaseItemQuantity(prod2.getItemNumber(), stock);
            cart.decreaseItemQuantity(prod2.getItemNumber(), stock);
            // pontosan 2 prod1, és 8 prod2 kell, legyen
            assertEquals(2, cart.productItemList().get(INDEX_PROD1).getQuantity());
            assertEquals(8, cart.productItemList().get(INDEX_PROD2).getQuantity());

        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arra vagyok kíváncsi, hogy ha lezárom a kosarat, akkor a létrejött
     * Order-ben megvannak-e a termékek
     */
    @Test
    void testCloseCart(){
        Cart cart = new Cart();
        final int INDEX_PROD1 = 0;
        final int INDEX_PROD2 = 1;
        try {
            // fogalmam sincs, mi van a raktárban, az előző tesztek után,
            // adok hozzá pár prod1-et és prod2-t
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 10);
            // hozzáadok a kosárhoz pár terméket
            cart.addNewProduct(prod1.getItemNumber(), 5, stock);
            cart.addNewProduct(prod2.getItemNumber(), 10, stock);
            // a kosárban 2 termék van
            assertEquals(2, cart.productItemList().size());
            // ... és pontosan 5 prod1, és 10 prod2
            assertEquals(5, cart.productItemList().get(INDEX_PROD1).getQuantity());
            assertEquals(10, cart.productItemList().get(INDEX_PROD2).getQuantity());

            OrderOnline order = (OrderOnline) cart.closeCart(ShoppingModeEnum.ONLINE);
            // az order-ben pontosan 5 prod1, és 10 prod2 kell, legyen
            assertEquals(5,  order.getOrderItems().get(INDEX_PROD1).getQuantity());
            assertEquals(10, order.getOrderItems().get(INDEX_PROD2).getQuantity());
            // kosár lezárva
            assertTrue(cart.getlosed());
        } catch (NotEnoughItemException | InvalidQuantityArgumentException | CartIsEmptyException | CartClosedException e) {
            e.printStackTrace();
        }
        // a kosár lezárása után a tartalma nem módosítható
        // kosár lezárva
        assertTrue(cart.getlosed());
        String message = MESSAGE_DEFAULT;
        try {
            // tehát ha hozzáadnék egyet
            cart.addNewProduct(prod1.getItemNumber(), 1, stock);
        } catch (NotEnoughItemException | CartClosedException e) {
            message = e.getMessage();
        }
        // az "Kosár lezárva"
        assertTrue(message.contains("A kosár lezárva"));
    }
}