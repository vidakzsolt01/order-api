package hu.gov.allamkincstar.training.javasebsc.orderapi.interfaces;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;

import java.util.ArrayList;
import java.util.List;

public interface ProductContainerHandler {

    void registerNewItem(ProductItem item) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException;
    void addSomeMoreQuantity(String itemNumber, int quantity) throws InvalidIncreaseArgumentException;
    void removeItem(String itemNumber);
    ProductItem findItem(String itemNumber);
    ProductItem searchItem(String itemNumber);
    boolean isProductExist(String itemNumber);
    void changeItemQuantity(String itemNumber, int quantity) throws NotEnoughItemException, InvalidIncreaseArgumentException;
    void disposeEmptyItem(ProductItem productItem) throws NoItemFoundException, NotEmptyItemException;
    ArrayList productItemList();

}
