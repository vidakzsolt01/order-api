package hu.gov.allamkincstar.training.javasebsc.orderapi.interfaces;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;

public interface ProductContainerHandler {

    void registerNewItem(ProductItem item) throws ItemExistsWithNameException, ItemExistsWithItemNumberException;
    void addItem(String itemNumber, int quantity);
    void removeItem(String itemNumber);
    ProductItem findItem(String itemNumber);
    ProductItem searchItem(String itemNumber);
    boolean isProductExist(Product product);
    void changeItemQuantity(String lotIndex, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(ProductItem productItem) throws NoItemFoundException, NotEmptyItemException;

}
