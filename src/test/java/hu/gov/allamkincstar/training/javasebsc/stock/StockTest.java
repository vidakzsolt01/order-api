package hu.gov.allamkincstar.training.javasebsc.stock;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Executable;

import static org.junit.jupiter.api.Assertions.*;

class StockTest extends Container {

    Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    Stock stock = new Stock(prod1, 10);

    @AfterEach
    void tearDown() {
    }

    @Test
    void registerNewItem() {
        stock.depositProduct(prod2, 10);
        assertEquals(2, stock.getProductItems().size());
        assertEquals(10, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
        stock.depositProduct(prod1, 10);
        assertEquals(20, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
    }

    @Test
    void removeItem() {
        // ilyen nincs a Stock-nak (nem része a feladatnak)
        assertEquals(1, stock.getProductItems().size());
    }

    @Test
    void findItem() {
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.bookProduct(prod2, 20));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertTrue(message.equals(realMessage));
        boolean error = false;
        try {
            stock.bookProduct(prod1, 30);
        } catch (NotEnoughItemException e) {
            error = true;
        }
        assertTrue(error);

        try {
            stock.bookProduct(prod1, 9);
            error = false;
        } catch (NotEnoughItemException e) {
            error = true;
        }
        assertFalse(error);
    }

    @Test
    void isProductExist() {
    }

    @Test
    void changeItemQuantity() {
    }

    @Test
    void disposeEmptyItem() {
    }

    @Test
    void bookProduct() {
    }

    @Test
    void depositProduct() {
    }

    @Test
    void getItems() {
    }
}