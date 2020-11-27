package exceptions;

import baseclasses.Lot;

public class NoItemFounException extends Exception{

    private static final String DEFAULT_MESSAGE = "A keresett termék nem található.";

    public NoItemFounException() {
        super(DEFAULT_MESSAGE);
    }

    public NoItemFounException(String message, Lot item) {
        super(message+" Termék: "+item.getProduct().getItemName());
    }

    public NoItemFounException(Lot item) {
        this(DEFAULT_MESSAGE, item);
    }

}
