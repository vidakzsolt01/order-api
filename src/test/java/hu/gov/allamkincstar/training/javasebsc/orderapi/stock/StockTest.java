package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest extends ProductContainer {

    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prodNameWrong = new Product("333333", "Termék-2", 2500, 5);
    static Stock stock = new Stock();

    @BeforeAll
    static void prolog() {
        try {
            stock.depositProduct(prod1, 10);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
            e.printStackTrace();
        }
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
        assertEquals(realMessage, message);

        boolean error = false;
        try {
            stock.bookProduct(prod1.getItemNumber(), -1);
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
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.bookProduct(prod2.getItemNumber(), 20));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertEquals(realMessage, message);

        // 10 darabnak kellene lennie prod1-ből
        assertEquals(10, stock.getBookableQuantity(prod1.getItemNumber()));
        // 0 darab foglaltnak kellene lennie prod1-ből
        assertEquals(0, stock.getBookedQuantity(prod1.getItemNumber()));

        boolean error = false;
        try {
            stock.bookProduct(prod1.getItemNumber(), 11);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertTrue(error);

        try {
            stock.bookProduct(prod1.getItemNumber(), 10);
            error = false;
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertFalse(error);

        try {
            stock.depositProduct(prod1, 100);
            assertEquals(100, stock.getBookableQuantity(prod1.getItemNumber()));
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
            e.printStackTrace();
        }

        try {
            stock.bookProduct(prod1.getItemNumber(), 10);
            error = false;
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertFalse(error);
    }

    @Test
    void depositProduct() {
        String message = "nincshiba";
        try {
            stock = new Stock(prod1, 100);
            assertEquals(1, stock.getProductItems().size());
            stock.depositProduct(prod2, 10);
            assertEquals(2, stock.getProductItems().size());
            assertEquals(100, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
            stock.depositProduct(prod1, 10);
            assertEquals(110, stock.getProductItems().get(prod1.getItemNumber()).getQuantity());
            stock.depositProduct(prodNameWrong, 10);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
            message = e.getMessage();
        }
        assertEquals(2, stock.getProductItems().size());
        assertTrue(message.startsWith("Ez a termék már létezik másik cikkszámmal."));
    }

    @Test
    void getBookableQuantity() throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        stock = new Stock(prod1, 10);
    }
}