package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StockItemTest extends Container{
    static StockItem stockItem;

    @BeforeEach
    void prolog(){
        stockItem = new StockItem(new Product("111111", "Termék-1", 1190), 10);
    }

    @Test
    void bookSomeQuantity(){
        assertEquals(10, stockItem.getBookableQuantity());
        assertEquals(0, stockItem.getBookedQuantity());
        try {
            stockItem.bookSomeQuantity(9);
            assertEquals(1, stockItem.getBookableQuantity());
            assertEquals(9, stockItem.getBookedQuantity());
        } catch (NotEnoughItemException e) {
            e.printStackTrace();
        }
    }

    @Test
    void releaseBookedQuantity(){
    }

    @Test
    void getBookedQuantity(){
    }

    @Test
    void getBookableQuantity(){
    }

    @Test
    void finishBook(){
    }
}