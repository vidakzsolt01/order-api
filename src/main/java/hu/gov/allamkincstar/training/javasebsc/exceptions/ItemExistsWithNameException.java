package hu.gov.allamkincstar.training.javasebsc.exceptions;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;

public class ItemExistsWithNameException extends Exception{

    private static final String DEFAULT_MESSAGE = "Ez a termék már létezik másik cikkszámmal.";

    public ItemExistsWithNameException(Product existing) {
        super(DEFAULT_MESSAGE+" Létező termék: "+existing.getItemName()+", cikkszám: "+existing.getItemNumber());
    }

}
