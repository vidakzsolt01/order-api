package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

public class NotEnoughItemException extends Exception{

    private static final String DEFAULT_MESSAGE = "Nincs elegendő termék.";

    public NotEnoughItemException() {
        this(DEFAULT_MESSAGE);
    }

    public NotEnoughItemException(String message) {
        super(message);
    }
}
