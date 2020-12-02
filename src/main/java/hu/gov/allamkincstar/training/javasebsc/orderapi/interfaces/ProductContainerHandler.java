package hu.gov.allamkincstar.training.javasebsc.orderapi.interfaces;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;

public interface ProductContainerHandler {

    void registerNewItem(Lot item) throws ItemExistsWithNameException, ItemExistsWithItemNumberException;
    void addItem(String itemNumber, int quantity);
    void removeItem(String itemNumber);
    Lot findItem(String itemNumber);
    Lot searchItem(String itemNumber);
    boolean isProductExist(Product product);
    void changeItemQuantity(String lotIndex, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(Lot lot) throws NoItemFoundException, NotEmptyItemException;

}
