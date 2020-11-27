package Interfaces;

import dto.Lot;
import exceptions.NoItemFounException;
import exceptions.NotEnoughItemException;

public interface ContainerHandler {

    void registerNewItem(Lot lot);
    void removeItem(Lot lot) throws NoItemFounException;
    void changeItemQuantity(Lot lot, Integer quantity) throws NotEnoughItemException;
    void disposeEmptyItem(Lot lot) throws NoItemFounException;

}
