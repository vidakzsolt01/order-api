package Interfaces;

import baseclasses.Lot;
import exceptions.NoItemFoundException;
import exceptions.NotEnoughItemException;

public interface ContainerHandler {

    void registerNewItem(Lot lot);
    void removeItem(String lotIndex) throws NoItemFoundException;
    Lot findItem(String lotIndex);
    void changeItemQuantity(String lotIndex, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(String lotIndex) throws NoItemFoundException;

}
