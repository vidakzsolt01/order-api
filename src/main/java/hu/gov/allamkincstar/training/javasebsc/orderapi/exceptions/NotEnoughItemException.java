package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

public class NotEnoughItemException extends Exception{

    private static final String DEFAULT_MESSAGE = "Nincs elegendő termék.";

    public NotEnoughItemException(ProductItem item, Integer quantity) {
        this(DEFAULT_MESSAGE, item, quantity);
    }

    public NotEnoughItemException(String message, ProductItem item, Integer quantity) {
        super(message+" Termék: "+item.getProduct().getItemName()+", kívánt módosítás: "+quantity);
    }
}
