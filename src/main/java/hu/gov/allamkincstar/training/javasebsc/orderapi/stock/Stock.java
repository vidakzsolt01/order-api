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
    public List<ProductItem> productItemList(){
        List<ProductItem> itemList = new ArrayList<>();
        productItems.forEach((key, value) -> itemList.add(new StockItem((StockItem) value)));
        return itemList;
    }

    public Stock(Product product, int quantity) throws InvalidQuantityArgumentException {
        super();
        registerNewItem(new StockItem(product, quantity));
    }

    public void depositProduct(Product product, int quantity) throws InvalidQuantityArgumentException {
        if (quantity <= 0) throw new InvalidQuantityArgumentException();
        registerNewItem(new StockItem(product, quantity));
    }

    public OrderItem bookProduct(String itemNumber, int quantity) throws InvalidQuantityArgumentException, NotEnoughItemException {
        if (quantity <= 0) throw new InvalidQuantityArgumentException();
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return  new OrderItem(stockItem.bookSomeQuantity(quantity), quantity);
    }

/*
    public void releaseBookedQuantity(String itemNumber, int quantityToRelease) throws InvalidQuantityArgumentException, NotEnoughItemException {
        if (quantityToRelease <= 0) throw new InvalidQuantityArgumentException();
        ((StockItem) findItem(itemNumber)).releaseBookedQuantity(quantityToRelease);
    }
*/

    public void releaseBookedQuantity(OrderItem itemToRelease) throws NotEnoughItemException {
        ((StockItem) findItem(itemToRelease.getIndex())).releaseBookedQuantity(itemToRelease.getQuantity());
    }

    public int getBookedQuantity(String itemNumber){
        return ((StockItem) findItem(itemNumber)).getBookedQuantity();
    }

    public int getBookableQuantity(String itemNumber){
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return stockItem.getBookableQuantity();
    }

    public void finishItemBook(ProductItem itemToFinish) throws NotEnoughItemException, InvalidQuantityArgumentException {
        ((StockItem) findItem(itemToFinish.getIndex())).finishBook(itemToFinish.getQuantity());
    }

}
