package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest extends ProductContainer {

    static Product prod1 = new Product("111111", "Termék-1", 1000, 27);
    static Product prod2 = new Product("222222", "Termék-2", 2000, 5);
    static Product prod3 = new Product("333333", "Termék-3", 2000, 5);
    static Product prodFailNumber = new Product("444444", "Termék-2", 2000, 12);
    static Product prodFailName = new Product("222222", "Termék-3", 2000, 12);
    static Stock stock = new Stock();

    /**
     * Arra vagyok kiváncsi, hogy
     * <ul>
     *     <li>a létező terméket megtaláljuk-e, és</li>
     *     <li>a neml étező termékre RuntimeException-t dobunk-e</li>
     * </ul>
     */
    @Test
    void findItem() {
        // raktározok pár terméket
        stock = new Stock();
        stock.depositProduct(prod1, 10);
        stock.depositProduct(prod2, 20);

        // megkeresem a prod1-et
        ProductItem item = stock.findItem(prod1.getItemNumber());
        // kell találnom belő 10 darabot
        assertEquals(10, item.getQuantity());

        // megkeresem a prod2-et
        item = stock.findItem(prod2.getItemNumber());
        // kell találnom belőle 20 darabot
        assertEquals(20, item.getQuantity());

        // és ha prod3-at keresek, akkor az RuntimeException
        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.findItem(prod3.getItemNumber()));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertEquals(realMessage, message);
    }

    @Test
    void isProductExist() {
        assertTrue(stock.isProductExist(prod1.getItemNumber()));
        assertFalse(stock.isProductExist(prod2.getItemNumber()));
    }

    @Test
    void bookProduct() {
        // raktározok pár terméket
        stock = new Stock();
        stock.depositProduct(prod1, 10);
        stock.depositProduct(prod2, 20);

        Exception exception = assertThrows(NoItemFoundException.class, ()-> stock.bookProduct(prod2.getItemNumber(), 20));
        String message = "A keresett termék nem található.";
        String realMessage = exception.getMessage();
        assertEquals(realMessage, message);
        try {
            stock = new Stock(prod1, 10);
            // 10 darabnak kellene lennie prod1-ből
            assertEquals(10, stock.getBookableQuantity(prod1.getItemNumber()));
            // 0 darab foglaltnak kellene lennie prod1-ből
            assertEquals(0, stock.getBookedQuantity(prod1.getItemNumber()));
            stock.bookProduct(prod1.getItemNumber(), 8);
            // 8 darab foglaltnak kellene lennie prod1-ből
            assertEquals(8, stock.getBookedQuantity(prod1.getItemNumber()));
            // 2 darab foglalhatónak kellene lennie prod1-ből
            assertEquals(2, stock.getBookableQuantity(prod1.getItemNumber()));
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            e.printStackTrace();
        }
        boolean error = false;
        try {
            stock.bookProduct(prod1.getItemNumber(), 11);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertTrue(error);

        try {
            stock.bookProduct(prod1.getItemNumber(), 2);
            error = false;
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            error = true;
        }
        assertFalse(error);

        try {
            stock.depositProduct(prod1, 100);
            assertEquals(100, stock.getBookableQuantity(prod1.getItemNumber()));
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException e) {
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
            assertEquals(1, stock.productItemList().size());
            stock.depositProduct(prod2, 10);
            assertEquals(2, stock.productItemList().size());
            assertEquals(100, stock.findItem(prod1.getItemNumber()).getQuantity());
            stock.depositProduct(prod1, 10);
            assertEquals(110, stock.findItem(prod1.getItemNumber()).getQuantity());
            stock.depositProduct(prodFailName, 5);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
            message = e.getMessage();
        }
        assertEquals(2, stock.productItemList().size());
        assertTrue(message.startsWith("Ezzel a cikkszámmal már létezik termék más névvel"));

        try {
            stock.depositProduct(prodFailNumber, 5);
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException e) {
            message = e.getMessage();
        }
        assertEquals(2, stock.productItemList().size());
        assertTrue(message.startsWith("Ezzel a névvel már létezik termék másik cikkszámon"));
    }

    @Test
    void getBookableQuantity() throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        stock = new Stock(prod1, 10);
        stock.depositProduct(prod1, 100);
        assertEquals(110, stock.getBookableQuantity(prod1.getItemNumber()));
        try {
            stock.bookProduct(prod1.getItemNumber(), 80);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            e.printStackTrace();
        }
        assertEquals(30, stock.getBookableQuantity(prod1.getItemNumber()));
        stock.depositProduct(prod1, 100);
        try {
            stock.bookProduct(prod1.getItemNumber(), 80);
        } catch (NotEnoughItemException | InvalidBookArgumentException e) {
            e.printStackTrace();
        }
        assertEquals(50, stock.getBookableQuantity(prod1.getItemNumber()));
    }

    @Test
    void testProductItemList(){
        try {
            stock = new Stock();
            stock.depositProduct(prod1, 100);
            assertEquals(1, stock.productItemList().size());
            stock.depositProduct(prod2, 10);
            assertEquals(2, stock.productItemList().size());
            stock.depositProduct(prod3, 10);
            assertEquals(3, stock.productItemList().size());
            stock.depositProduct(prod2, 10);
            assertEquals(3, stock.productItemList().size());
            assertEquals(20, ((StockItem)stock.productItemList().get(1)).getQuantity());
            stock.bookProduct(prod2.getItemNumber(), 3);
            assertEquals(20, ((StockItem)stock.productItemList().get(1)).getQuantity());
            int bookable = ((StockItem)stock.productItemList().get(1)).getBookableQuantity();
            assertEquals(17, bookable);
            assertEquals(3, ((StockItem)stock.productItemList().get(1)).getBookedQuantity());
        } catch (ItemExistsWithNameException | ItemExistsWithItemNumberException | InvalidIncreaseArgumentException | NotEnoughItemException | InvalidBookArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDepositProduct(){
    }

    @Test
    void testBookProduct(){
    }

    @Test
    void releaseBookedQuantity(){
    }

    @Test
    void getBookedQuantity(){
    }

    @Test
    void testGetBookableQuantity(){
    }

    @Test
    void finishItemBook(){
    }

    @Override
    public ArrayList productItemList(){
        return null;
    }
}