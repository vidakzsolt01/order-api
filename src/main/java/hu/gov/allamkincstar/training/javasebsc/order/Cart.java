package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.stock.Stock;

import java.util.Map;

public class Cart extends ProductContainer {

    public Cart() {
    }

    public Cart(OrderItem item) {
        super(item);
    }

    public Map<String, Lot> increaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (productItems.get(item.getIndex()) != item){
            throw new NoItemFoundException(item);
        }
        changeItemQuantity(item.getIndex(), 1);
        return productItems;
    }

    public Map<String, Lot> decreaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (productItems.get(item.getIndex()) != item) throw new NoItemFoundException(item);
        changeItemQuantity(item.getIndex(), -1);
        return productItems;
    }

    public Order closeCart(Stock stock){
        //TODO itt kell vizsgálni, hogy a termékösszeválogatás
        // végeztével van-e elegendő raktárkészlet

        return new Order(productItems);
    }

}
