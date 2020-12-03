package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

public class InvalidOrderOperationException extends Exception{
    private static final String DEFAULT_MESSAGE = "Érvénytelen tevékenység.";

    public InvalidOrderOperationException(ProductItem item, Integer quantity) {
        this(DEFAULT_MESSAGE);
    }

    public InvalidOrderOperationException(String message) {
        super(DEFAULT_MESSAGE + " " +message);
    }
}
