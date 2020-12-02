package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private static final String ITEM_NUMBER = "111111";
    private static final String ITEM_NAME = "Termék-1";
    private static final Integer UNIT_PRICE = 1200;
    private static final Integer VAT_PERCENT = 27;

    Product product = new Product(ITEM_NUMBER, ITEM_NAME, UNIT_PRICE, VAT_PERCENT);

    @Test
    void getItemNumber() {
        assertEquals(ITEM_NUMBER, product.itemNumber, "ItemNumber: "+product.getItemNumber());
    }

    @Test
    void getItemName() {
        assertEquals(product.itemName, ITEM_NAME);
    }

    @Test
    void getNetUntiPrice() {
        assertEquals(product.netUntiPrice, UNIT_PRICE);
    }

    @Test
    void setNetUntiPrice() {
        product.setNetUntiPrice(product.getNetUntiPrice()+1200);
        assertEquals(product.netUntiPrice, UNIT_PRICE+1200);
    }

    @Test
    void getVATPercent() {
        assertEquals(product.VATPercent, VAT_PERCENT, "getVATPercent() is OK");
    }
}