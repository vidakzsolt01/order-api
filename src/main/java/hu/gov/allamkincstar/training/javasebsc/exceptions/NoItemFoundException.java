package hu.gov.allamkincstar.training.javasebsc.exceptions;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;

public class NoItemFoundException extends RuntimeException{

    private static final String DEFAULT_MESSAGE = "A keresett termék nem található.";

    public NoItemFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public NoItemFoundException(String message, Lot item) {
        super(message+" Termék: "+item.getProduct().getItemName());
    }

    public NoItemFoundException(Lot item) {
        this(DEFAULT_MESSAGE, item);
    }

}
