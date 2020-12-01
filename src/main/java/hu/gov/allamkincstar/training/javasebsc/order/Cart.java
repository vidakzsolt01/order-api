package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Order;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.ShoppingModeEnum;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.stock.Stock;

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

    public void decreaseItemQuantity(OrderItem item, Stock stock) throws NotEnoughItemException, InvalidBookArgumentException {
        modifyQuantity(item, stock, -1);
    }

    private void modifyQuantity(OrderItem item, Stock stock, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        stock.bookProduct(item.getProduct(), quantity);
        if (findItem(item.getIndex()) != item) throw new NoItemFoundException(item);
        changeItemQuantity(item.getIndex(), -1);
    }

    public Order closeCart(ShoppingModeEnum shoppingMode){
        return (shoppingMode == ShoppingModeEnum.DIRECT) ? new OrderDirect(getProductItems()) : new OrderOnline(getProductItems(), new DeliveryParameters());
    }

}
