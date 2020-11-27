package dto.stock;

import dto.Lot;
import dto.Product;
import dto.order.OrderItem;

import java.util.Map;

public class Stock {

    private Map<String, StockItem> stockItems;

    public void stockInItem(StockItem itemToStockin){
        if (stockItems.containsKey(itemToStockin.getProduct().getItemNumber())){
            StockItem item = stockItems.get(itemToStockin.getProduct().getItemNumber());
            item.setQuantity(item.getQuantity() + itemToStockin.getQuantity());
        } else {
            stockItems.put(itemToStockin.getProduct().getItemNumber(), itemToStockin);
        }
    }

    public OrderItem expendItem(Product product, Integer quantity){
        if (stockItems.containsKey(product.getItemNumber())){
            StockItem item = stockItems.get(product.getItemNumber());
            if (item.getQuantity() >= quantity){
                return new OrderItem(product, quantity);
            }
        }
        return null;
    }

}
