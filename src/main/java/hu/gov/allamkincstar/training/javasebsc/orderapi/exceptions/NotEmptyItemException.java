package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Lot;

public class NotEmptyItemException extends Exception{

    private static final String DEFAULT_MESSAGE = "A terméktétel mennyisége nem 0, nem törölhető.";

    public NotEmptyItemException() {
        super(DEFAULT_MESSAGE);
    }

    public NotEmptyItemException(String message, Lot item) {
        super(message+" Termék: "+item.getProduct().getItemName());
    }

    public NotEmptyItemException(Lot item) {
        this(DEFAULT_MESSAGE, item);
    }

}
