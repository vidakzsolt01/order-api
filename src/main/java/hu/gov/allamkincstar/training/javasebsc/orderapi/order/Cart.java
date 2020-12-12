package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public final class Cart extends ProductContainer {

    public Cart() {
    }

    @Override
    public List<ProductItem> productItemList() {
        List<ProductItem> itemList = new ArrayList<>();
        productItems.forEach( (key, value) -> itemList.add(new OrderItem(value)));
        return itemList;
    }

    public OrderItem addNewProduct(String itemNumber, int quantity, Stock stock) throws NotEnoughItemException, InvalidQuantityArgumentException {
        OrderItem item = new OrderItem(stock.bookProduct(itemNumber, quantity));
        registerNewItem(item);
        return item;
    }

    public void removeProduct(String itemNumber, Stock stock){
        OrderItem item = (OrderItem) findItem(itemNumber);
        try {
            // itt nem akarok exception-öket dobálni, mert:
            // na nincs annyi lefoglalva, akkor... asse' érdekes, a kosárból törölhetjük
            stock.releaseBookedQuantity(item);
        } catch (NotEnoughItemException e) {
        // id azér' valami log-ot csak dobjunk
            System.out.println("Nincs elég foglalt mennyiség a raktárban a kosárból törlendő termék mennyiségénak felszabadításához");
        }
        productItems.remove(itemNumber);
    }

    public OrderItem increaseItemQuantity(String itemNumber, Stock stock) throws NotEnoughItemException, InvalidQuantityArgumentException{
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.bookProduct(item.getProduct().getItemNumber(), 1);
        item.increaseQuantity(1);
        return item;
    }

    public void decreaseItemQuantity(String itemNumber, Stock stock) throws NotEnoughItemException, InvalidQuantityArgumentException {
        OrderItem item = (OrderItem) findItem(itemNumber);
        stock.releaseBookedQuantity(new OrderItem(item.getProduct(), 1));
        item.decreaseQuantity(1);
        if (item.getQuantity() == 0) removeItem(item.getProduct().getItemNumber());
    }

    public Order closeCart(ShoppingModeEnum shoppingMode) throws CartIsEmptyException {
        if (productItems.size() == 0) throw new CartIsEmptyException();
        long id = OrderRegister.getNextOrderId();
        return (shoppingMode == ShoppingModeEnum.DIRECT) ? new OrderDirect(id, this.productItemList()) : new OrderOnline(id, this.productItemList(), new DeliveryParameters());
    }

}
