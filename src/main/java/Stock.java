import java.util.Map;

public class Stock {
    private Map<String, StockItem> stockItems;

    public void getInProduct(Product product, Integer quantity){
        if (stockItems.containsKey(product.getItemNumber())){
            StockItem item = stockItems.get(product.getItemNumber());
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            stockItems.put(product.getItemNumber(), new StockItem(product, quantity));
        }
    }

    public void 

}
