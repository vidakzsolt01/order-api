package hu.gov.allamkincstar.training.javasebsc.interfaces;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

public interface ContainerHandler {

    void registerNewItem(Product product, int quantity);
    void removeItem(String lotIndex) throws NoItemFoundException;
    Lot findItem(String lotIndex);
    boolean isProductExist(String index);
    void changeItemQuantity(String lotIndex, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(String lotIndex) throws NoItemFoundException;

}
