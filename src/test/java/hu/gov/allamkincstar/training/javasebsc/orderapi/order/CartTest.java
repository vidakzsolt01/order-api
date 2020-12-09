package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CartTest extends Container {

    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 12);
    static Product prod4 = new Product("444444", "Termék-4", 10, 27);
    static Stock stock = new Stock();
    Cart cart = new Cart();

    @BeforeAll
    static void beforeall(){
        try {
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 20);
            stock.depositProduct(prod3, 30);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
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
        // 0 termék van a listában
        assertEquals(0, cart.productItemList().size());
        try {
            // hozzáadunk 5 prod1-et
            cart.addNewProduct(prod1, 5, stock);
            // 1 termék van a listában
            assertEquals(1, cart.productItemList().size());
            // hozzáadunk 10 prod2-t
            cart.addNewProduct(prod2, 10, stock);
            // 2 termék van a listában
            assertEquals(2, cart.productItemList().size());
            // hozzáadunk további 5 prod1-et
            cart.addNewProduct(prod1, 5, stock);
            // továbbra is 2 termék van a listában
            assertEquals(2, cart.productItemList().size());
            // hozzáadok egy elemet
            cart.productItemList().add(new OrderItem(prod1, 10));
            assertEquals(2, cart.productItemList().size());
            // az 1. termékből 10 van a kosárlistában
            assertEquals(10, ((OrderItem)cart.productItemList().get(0)).getQuantity());
            ((OrderItem) cart.productItemList().get(0)).increaseQuantity(10);
            // még mindig 10 van a 0.-ból
            assertEquals(10, ((OrderItem)cart.productItemList().get(0)).getQuantity());
        } catch (NotEnoughItemException | InvalidBookArgumentException | InvalidIncreaseArgumentException | ItemExistsWithNameException | ItemExistsWithItemNumberException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddNewProduct(){
        try {
            cart.addNewProduct(prod1, 5, stock);
            assertEquals(1, cart.productItemList().size());
            cart.addNewProduct(prod2, 10, stock);
            assertEquals(2, cart.productItemList().size());
            cart.addNewProduct(prod1, 5, stock);
            assertEquals(2, cart.productItemList().size());
        } catch (NotEnoughItemException | InvalidBookArgumentException | InvalidIncreaseArgumentException | ItemExistsWithNameException | ItemExistsWithItemNumberException e) {
            e.printStackTrace();
        }
        assertEquals(2, cart.productItemList().size());
    }

    @Test
    void removeProduct(){
    }

    @Test
    void testIncreaseItemQuantity(){
    }

    @Test
    void testDecreaseItemQuantity(){
    }

    @Test
    void testCloseCart(){
    }
}