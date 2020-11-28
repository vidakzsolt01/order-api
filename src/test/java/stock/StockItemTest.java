package stock;

import baseclasses.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockItemTest {

    private static final String ITEM_NUMBER = "111111";
    private static final String ITEM_NAME = "Termék-1";
    private static final Integer UNIT_PRICE = 1200;
    private static final Integer VAT_PERCENT = 27;
    private static final Integer QUANTITY = 125;

    StockItem stockItem = new StockItem(new Product(ITEM_NUMBER, ITEM_NAME, UNIT_PRICE), QUANTITY);

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