package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.ItemExistsWithItemNumberException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.ItemExistsWithNameException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.stock.Stock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CartTest extends Container {

    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 12);
    static Product prodFailName = new Product("444444", "Termék-2", 2000, 12);
    static Product prodFailNumber = new Product("333333", "Termék-2", 2000, 12);
    static Stock stock = new Stock();
    Cart cart = new Cart();

    @BeforeAll
    static void beforeall(){
        try {
            stock.depositProduct(prod1, 10);
            stock.depositProduct(prod2, 20);
            stock.depositProduct(prod3, 30);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addNewProduct() {
        try {
            cart.addNewProduct(prod1, 5, stock);
            assertEquals(1, cart.getProductItems().size());
            cart.addNewProduct(prod2, 10, stock);
            assertEquals(2, cart.getProductItems().size());
            cart.addNewProduct(prod1, 5, stock);
            assertEquals(2, cart.getProductItems().size());
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void removeItem() {
    }

    @Test
    void increaseItemQuantity() {
    }

    @Test
    void decreaseItemQuantity() {
    }

    @Test
    void closeCart() {
    }
}