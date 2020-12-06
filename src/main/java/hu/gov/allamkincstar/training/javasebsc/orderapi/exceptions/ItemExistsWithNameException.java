package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class ItemExistsWithNameException extends Exception{

    private static final String DEFAULT_MESSAGE = "Ezzel a névvel már létezik termék másik cikkszámon.";

    public ItemExistsWithNameException(String itemNumberExisting) {
        super(DEFAULT_MESSAGE+" Létező termék cikkszáma: "+itemNumberExisting);
    }

}
