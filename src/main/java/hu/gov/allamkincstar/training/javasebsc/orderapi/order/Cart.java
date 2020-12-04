package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Order;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidIncreaseArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

public class Cart extends ProductContainer {

    public Cart() {
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

    public void decreaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException, InvalidIncreaseArgumentException {
        modifyQuantity(item, stock, -1);
    }

    private void modifyQuantity(OrderItem item, Stock stock, int quantity) throws NotEnoughItemException, InvalidBookArgumentException, InvalidIncreaseArgumentException {
        if (item.getQuantity() < 0){
            stock.re
        }
        stock.bookProduct(item.getProduct(), quantity);
        if (findItem(item.getIndex()) != item) throw new NoItemFoundException(item);
        changeItemQuantity(item.getIndex(), quantity);
    }

    public Order closeCart(ShoppingModeEnum shoppingMode){
        return (shoppingMode == ShoppingModeEnum.DIRECT) ? new OrderDirect(getProductItems()) : new OrderOnline(getProductItems(), new DeliveryParameters());
    }

}
