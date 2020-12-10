package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class InvalidQuantityArgumentException extends Exception{
    private static final String DEFAULT_MESSAGE = "A 'mennyiség' értéke nem lehet egynél kisebb";

    public InvalidQuantityArgumentException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidQuantityArgumentException(String message) {
        super(message);
    }
}
