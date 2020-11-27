package exceptions;

import dto.Lot;
import order.OrderItem;

public class NotEnoughItemException extends Exception{

    private static final String DEFAULT_MESSAGE = "Nincs elegendő termék.";

    public NotEnoughItemException(Lot item, Integer quantity) {
        this(DEFAULT_MESSAGE, item, quantity);
    }

    public NotEnoughItemException(String message, Lot item, Integer quantity) {
        super(message+" Termék: "+item.getProduct().getItemName()+", kívánt módosítás: "+quantity);
    }
}
