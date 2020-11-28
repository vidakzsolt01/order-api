package order;

import baseclasses.Container;
import baseclasses.Lot;
import exceptions.NoItemFoundException;
import exceptions.NotEnoughItemException;
import stock.Stock;

import java.util.Map;

public class Cart extends Container {

    public Cart() {
    }

    public Cart(OrderItem item) {
        super(item);
    }

    public Map<String, Lot> increaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item){
            throw new NoItemFoundException(item);
        }
        changeItemQuantity(item.getIndex(), 1);
        return containerItems;
    }

    public Map<String, Lot> decreaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item) throw new NoItemFoundException(item);
        changeItemQuantity(item.getIndex(), -1);
        return containerItems;
    }

    public Order closeCart(Stock stock){
        //TODO itt kell vizsgálni, hogy a termékösszeválogatás
        // végeztével van-e elegendő raktárkészlet

        return new Order(containerItems);
    }

}
