package stock;

import Interfaces.StoreHandler;
import dto.Lot;
import dto.Product;
import order.OrderItem;
import exceptions.NoStockItemFoundInStockException;
import exceptions.NotEnoughSortItemInStockException;

import java.util.Map;

public class Stock implements StoreHandler {

    private Map<String, StockItem> stockItems;

    @Override
    public void addNewItem(Lot itemToStockin) {
        if (stockItems.containsKey(itemToStockin.getProduct().getItemNumber())){
            StockItem item = stockItems.get(itemToStockin.getProduct().getItemNumber());
            item.setQuantity(item.getQuantity() + itemToStockin.getQuantity());
        } else {
            stockItems.put(itemToStockin.getProduct().getItemNumber(), (StockItem) itemToStockin);
        }
    }

    @Override
    public void removeItem(Lot item) {
        stockItems.remove(item.getProduct().getItemNumber());
    }

    @Override
    public void changeItenQuantity(Lot lot, Integer quantity) {

    }

    @Override
    public void checkZeroQuantity(Lot item) {
        if (item.getQuantity() == 0) removeItem(item);
    }

    public OrderItem expendItem(Product product, Integer quantity) throws NoStockItemFoundInStockException, NotEnoughSortItemInStockException {
        if (!stockItems.containsKey(product.getItemNumber()))
            throw new NoStockItemFoundInStockException(product);

        StockItem item = stockItems.get(product.getItemNumber());
        if (item.getQuantity() < quantity)
            throw new NotEnoughSortItemInStockException(item, quantity);
        OrderItem result = new OrderItem(product, quantity);

        return result;
    }


}
