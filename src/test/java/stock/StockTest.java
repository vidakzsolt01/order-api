package stock;

import baseclasses.Product;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StockTest extends Container {

    Stock stock = new Stock(new StockItem(new Product("111111", "Termék-1", 1200), 125));

    @Test
    void base() {
        assertEquals(1, stock.getItems().size());
        assertEquals("Termék-1", stock.getItems().get("111111").getProduct().getItemName());
        //assert "Termékdarab: "+stock.getItems().get("111111").getQuantity() == "";
        stock.registerNewItem(new StockItem(new Product("222222", "Termék-2", 19499), 1140));
        assertEquals("Termék-2", stock.getItems().get("222222").getProduct().getItemName());
    }
    @Test
    void expendItem() {
        stock.registerNewItem(new StockItem(new Product("222222", "Termék-2", 19499), 1000));
    }
}