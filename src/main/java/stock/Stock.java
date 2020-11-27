package stock;

import dto.Container;
import dto.Lot;
import dto.Product;
import exceptions.NoItemFounException;
import exceptions.NotEnoughItemException;
import order.OrderItem;

import java.util.Map;

public class Stock extends Container {

    public Stock() {
    }

    public Stock(Lot item) {
        super(item);
    }

    /*
        private Map<String, StockItem> stockItems;

        @Override
        public void registerNewItem(Lot itemToStockin) {
            if (stockItems.containsKey(itemToStockin.getProduct().getItemNumber())){
                StockItem item = stockItems.get(itemToStockin.getProduct().getItemNumber());
                item.setQuantity(item.getQuantity() + itemToStockin.getQuantity());
            } else {
                stockItems.put(itemToStockin.getProduct().getItemNumber(), (StockItem) itemToStockin);
            }
        }

        @Override
        public void removeItem(Lot item) throws NoItemFounException {
            if (!stockItems.containsKey(item.getProduct().getItemNumber())) throw new NoItemFounException();
            stockItems.remove(item.getProduct().getItemNumber());
        }

        @Override
        public void changeItemQuantity(Lot item, Integer quantity) throws NotEnoughItemException {
            item.changeItemQuantity(quantity);
        }

        @Override
        public void disposeEmptyItem(Lot item) throws NoItemFounException {
            if (item.getQuantity() == 0) removeItem(item);
        }

    */
    public OrderItem expendItem(StockItem item, Integer quantity) throws NoItemFounException, NotEnoughItemException {
        if (!containerItems.containsKey(item.getIndex()))
            throw new NoItemFounException(item);

        StockItem itemInStock = (StockItem) containerItems.get(item.getIndex());
        if (itemInStock.getQuantity() < quantity)
            throw new NotEnoughItemException(itemInStock, quantity);

        OrderItem result = new OrderItem(new Product(item.getProduct()), quantity);

        return result;
    }

    public Map<String, Lot> getItems(){
        return containerItems;
    }

}
