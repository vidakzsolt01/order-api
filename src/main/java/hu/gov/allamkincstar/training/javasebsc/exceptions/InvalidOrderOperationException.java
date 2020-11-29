package hu.gov.allamkincstar.training.javasebsc.exceptions;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;

public class InvalidOrderOperationException extends Exception{
    private static final String DEFAULT_MESSAGE = "Érvénytelen rendelés-tevékenység.";

    public InvalidOrderOperationException(Lot item, Integer quantity) {
        this(DEFAULT_MESSAGE);
    }

    public InvalidOrderOperationException(String message) {
        super(message);
    }
}
