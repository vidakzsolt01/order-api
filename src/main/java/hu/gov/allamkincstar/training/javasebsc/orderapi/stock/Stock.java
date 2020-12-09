package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public final class Stock extends ProductContainer {

    public Stock() {
        super();
    }

    @Override
    public ArrayList productItemList(){
        ArrayList itemList = new ArrayList<StockItem>();
        productItems.forEach((key, value) -> itemList.add(new StockItem((StockItem) value)));
        return itemList;
    }

    public Stock(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        super();
        registerNewItem(new StockItem(product, quantity));
    }

    public void depositProduct(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        registerNewItem(new StockItem(product, quantity));
    }

    public OrderItem bookProduct(String itemNumber, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        if (quantity <= 0) throw new InvalidBookArgumentException(quantity);
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return  new OrderItem(stockItem.bookSomeQuantity(quantity), quantity);
    }

    public void releaseBookedQuantity(String itemNumber, int quantityToRelease){
        ((StockItem) findItem(itemNumber)).releaseBookedQuantity(quantityToRelease);
    }

    public int getBookedQuantity(String itemNumber){
        return ((StockItem) findItem(itemNumber)).getBookedQuantity();
    }

    public int getBookableQuantity(String itemNumber){
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return stockItem.getBookableQuantity();
    }

    public void finishItemBook(String itemNumber, int quantity) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        ((StockItem) findItem(itemNumber)).finishBook(quantity);
    }

}
