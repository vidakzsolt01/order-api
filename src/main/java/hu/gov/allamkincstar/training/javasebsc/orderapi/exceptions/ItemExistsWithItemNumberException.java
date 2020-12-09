package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

/**
 * hibás program okozhatja: RuntimeException lesz
 */
public class ItemExistsWithItemNumberException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Ezzel a cikkszámmal már létezik termék más névvel.";

    public ItemExistsWithItemNumberException(Product existing) {
        super(DEFAULT_MESSAGE+" Létező termék: "+existing.getItemName()+", cikkszám: "+existing.getItemNumber());
    }

}
