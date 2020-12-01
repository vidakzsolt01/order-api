package hu.gov.allamkincstar.training.javasebsc.stock;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidBookArgumentException;
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
        stock.registerNewItem(new Lot(prod2, 10));
        assertEquals(2, stock.getProductItems().size());
        assertEquals(10, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
        stock.registerNewItem(new Lot(prod1, 10));
        assertEquals(20, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
    }

    @Test
    void removeItem() {
        // ilyen nincs a Stock-nak (nem része a feladatnak)
        assertEquals(1, stock.getProductItems().size());
    }

    @Test
    void findItem() {
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.findItem(prod2.getItemNumber()));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertTrue(message.equals(realMessage));

        boolean error = false;
        try {
            stock.bookProduct(prod1, -1);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertTrue(error);

    }

    @Test
    void isProductExist() {
        assertTrue(stock.isProductExist(prod1));
        assertFalse(stock.isProductExist(prod2));
        assertTrue(stock.isProductExist(prod1.getItemNumber()));
        assertFalse(stock.isProductExist(prod2.getItemNumber()));
    }

    @Test
    void bookProduct() {
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.bookProduct(prod2, 20));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertTrue(message.equals(realMessage));

        // 10 darabnak kellene lennie prod1-ből
        assertEquals(10, stock.getBookableQuantity(prod1));
        // 0 darab foglaltnak kellene lennie prod1-ből
        assertEquals(0, stock.getBookedQuantity(prod1));

        boolean error = false;
        try {
            stock.bookProduct(prod1, 11);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertTrue(error);

        try {
            stock.bookProduct(prod1, 10);
            error = false;
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertFalse(error);

        stock.depositProduct(prod1, 100);
        assertEquals(100, stock.getBookableQuantity(prod1));

        try {
            stock.bookProduct(prod1, 10);
            error = false;
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertFalse(error);
    }

    @Test
    void depositProduct() {
        stock.depositProduct(prod2, 10);
        assertEquals(2, stock.getProductItems().size());
        assertEquals(10, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
        stock.depositProduct(prod1, 10);
        assertEquals(20, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
    }

    @Test
    void getItems() {
    }
}