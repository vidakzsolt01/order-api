package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Order;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

public class Cart extends ProductContainer {

    public Cart() {
    }

    public OrderItem addNewProduct(Product product, int quantity, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException, InvalidIncreaseArgumentException, ItemExistsWithNameException, ItemExistsWithItemNumberException {
        OrderItem item = new OrderItem( stock.bookProduct(product.getItemNumber(), quantity));
        registerNewItem(item);
        return item;
    }

    public void removeProduct(String itemNumber, Stock stock){
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.releaseBookedQuantity(itemNumber, item.getQuantity());
    }

    public OrderItem increaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException, InvalidIncreaseArgumentException {
        stock.bookProduct(item.getProduct().getItemNumber(), 1);
        item.increaseQuantity(1);
        return item;
    }

    public void decreaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        stock.releaseBookedQuantity(item.getProduct().getItemNumber(), 1);
        item.decreaseQuantity(1);
        if (item.getQuantity() == 0) removeItem(item.getProduct().getItemNumber());
    }

    public Order closeCart(ShoppingModeEnum shoppingMode){
        return (shoppingMode == ShoppingModeEnum.DIRECT) ? new OrderDirect(getProductItems()) : new OrderOnline(getProductItems(), new DeliveryParameters());
    }

}
