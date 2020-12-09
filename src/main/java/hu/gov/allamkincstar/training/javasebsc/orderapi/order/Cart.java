package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

import java.util.ArrayList;
import java.util.List;

public final class Cart extends ProductContainer {

    public Cart() {
    }

    @Override
    public ArrayList productItemList() {
        ArrayList itemList = new ArrayList<OrderItem>();
        productItems.forEach( (key, value) -> itemList.add(new OrderItem(value)));
        return itemList;
    }

    public OrderItem addNewProduct(Product product, int quantity, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException {
        OrderItem item = new OrderItem(stock.bookProduct(product.getItemNumber(), quantity));
        registerNewItem(item);
        return item;
    }

    public void removeProduct(String itemNumber, Stock stock){
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.releaseBookedQuantity(itemNumber, item.getQuantity());
        productItems.remove(itemNumber);
    }

    public OrderItem increaseItemQuantity(String itemNumber, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException, InvalidIncreaseArgumentException {
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.bookProduct(item.getProduct().getItemNumber(), 1);
        item.increaseQuantity(1);
        return item;
    }

    public void decreaseItemQuantity(String itemNumber, Stock stock) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.releaseBookedQuantity(item.getProduct().getItemNumber(), 1);
        item.decreaseQuantity(1);
        if (item.getQuantity() == 0) removeItem(item.getProduct().getItemNumber());
    }

    public Order closeCart(ShoppingModeEnum shoppingMode){
        return (shoppingMode == ShoppingModeEnum.DIRECT) ? new OrderDirect(this.productItemList()) : new OrderOnline(this.productItemList(), new DeliveryParameters());
    }

}
