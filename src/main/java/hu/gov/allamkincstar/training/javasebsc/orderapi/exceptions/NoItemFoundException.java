package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

/**
 * hibás program okozhatja: RuntimeException lesz
 */
public class NoItemFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "A keresett termék nem található.";

    public NoItemFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public NoItemFoundException(String message, ProductItem item) {
        super(message+" Termék: "+item.getProduct().getItemName());
    }

    public NoItemFoundException(ProductItem item) {
        this(DEFAULT_MESSAGE, item);
    }

}
