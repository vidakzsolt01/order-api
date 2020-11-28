package stock;

import baseclasses.Container;
import baseclasses.Lot;
import exceptions.NoItemFoundException;
import exceptions.NotEnoughItemException;
import order.OrderItem;

import java.util.Map;

public class Stock extends Container {

    public Stock() {
    }

    public Stock(Lot item) {
        super(item);
    }

    public OrderItem expendItem(String itemIndex, Integer quantity) throws NoItemFoundException, NotEnoughItemException {
        StockItem item = (StockItem) findItem(itemIndex);

        //OrderItem result = new OrderItem(item.book(quantity), quantity);

        return  new OrderItem(item.book(quantity), quantity);
    }

    //TODO Kell egy metódus, ami megmondja egy termékről,
    //     hogy van-e elegendő belőle

    public Map<String, Lot> getItems(){
        return containerItems;
    }

}
