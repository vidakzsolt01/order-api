package hu.gov.allamkincstar.training.javasebsc.stock;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StockTest extends Container {

    Stock stock;// = new Stock();

    @BeforeEach
    void setUp() {
        stock = new Stock(new Lot(new Product("111111", "Termék-1", 1000, 27), 10));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void registerNewItem() {
        stock.registerNewItem(new  Lot(new Product("222222", "Termék-2", 2000, 5), 10));
        assertEquals(2, stock.getItems().size());
    }

    @Test
    void removeItem() {
    }

    @Test
    void findItem() {
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