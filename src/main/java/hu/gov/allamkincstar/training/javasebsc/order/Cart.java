package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.stock.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cart extends ProductContainer {

    public Cart() {
    }

    public Cart(OrderItem item) {
        super(item);
    }

    public OrderItem addNewProduct(Product product, int quantity, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException {
        return stock.bookProduct(product, quantity);
    }

    public void removeItem(OrderItem item){
        removeItem(item);
    }

    public void increaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException {
        modifyQuantity(item, stock, 1);
    }

    public void decreaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException {
        modifyQuantity(item, stock, -1);
    }

    private void modifyQuantity(OrderItem item, Stock stock, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        stock.bookProduct(item.getProduct(), quantity);
        if (findItem(item.getIndex()) != item) throw new NoItemFoundException(item);
        changeItemQuantity(item.getIndex(), -1);
    }

    public Order closeCart(){
        return new Order(getProductItems(), new DeliveryParameters(), ShoppingModeEnum.ONLINE);
    }

}
