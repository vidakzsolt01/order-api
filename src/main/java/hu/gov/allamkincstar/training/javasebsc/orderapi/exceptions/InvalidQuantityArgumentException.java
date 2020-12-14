package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class InvalidQuantityArgumentException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "A 'mennyiség' nem lehet negatív";

    public InvalidQuantityArgumentException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidQuantityArgumentException(String message) {
        super(message);
    }
}
