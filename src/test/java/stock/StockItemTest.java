package stock;

import dto.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockItemTest {

    private static final String ITEM_NUMBER = "111111";
    private static final String ITEM_NAME = "Termék-1";
    private static final Integer UNIT_PRICE = 1200;
    private static final Integer VAT_PERCENT = 27;

    StockItem stockItem = new StockItem(new Product("111111", "Termék-1", 1200), 125);

/*
    @BeforeAll
    void setUp() {
        stockItem = new StockItem(new Product("111111", "Termék-1", 1200), 125);
    }
*/

    @Test
    void getProduct() {
        assertEquals(ITEM_NAME, stockItem.getProduct().getItemName());
    }

    @Test
    void getIndex() {
        assertEquals(ITEM_NUMBER, stockItem.getIndex());
    }

    @Test
    void getQuantity() {
        assertEquals(125, stockItem.getQuantity());
    }

    @Test
    void setQuantity() {
        stockItem.setQuantity(300);
        assertEquals(300, stockItem.getQuantity());
    }
}