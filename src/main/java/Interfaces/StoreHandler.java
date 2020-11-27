package Interfaces;

import dto.Lot;

public interface StoreHandler {

    void addNewItem(Lot lot);
    void removeItem(Lot lot);
    void changeItenQuantity(Lot lot, Integer quantity);
    void checkZeroQuantity(Lot lot);

}
