package Interfaces;

import baseclasses.Lot;
import exceptions.NoItemFounException;
import exceptions.NotEnoughItemException;

public interface ContainerHandler {

    void registerNewItem(Lot lot);
    void removeItem(Lot lot) throws NoItemFounException;
    void findItem(Lot lot);
    void changeItemQuantity(Lot lot, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(Lot lot) throws NoItemFounException;

}
