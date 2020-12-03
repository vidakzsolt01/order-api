package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class ItemExistsWithItemNumberException extends Exception{
    private static final String DEFAULT_MESSAGE = "Ezzel a cikkszámmal  már létezik másik termék.";

    public ItemExistsWithItemNumberException(Product existing) {
        super(DEFAULT_MESSAGE+" Létező termék: "+existing.getItemName()+", cikkszám: "+existing.getItemNumber());
    }

}