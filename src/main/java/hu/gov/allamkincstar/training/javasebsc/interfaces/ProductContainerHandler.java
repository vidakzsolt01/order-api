package hu.gov.allamkincstar.training.javasebsc.interfaces;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEmptyItemException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

public interface ProductContainerHandler {

    void registerNewItem(Lot item);
    void removeItem(String lotIndex);
    Lot findItem(String lotIndex);
    boolean isProductExist(Product product);
    void changeItemQuantity(String lotIndex, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(Lot lot) throws NoItemFoundException, NotEmptyItemException;

}
