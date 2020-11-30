package hu.gov.allamkincstar.training.javasebsc.exceptions;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;

public class InvalidBookArgumentException extends Exception{
    private static final String DEFAULT_MESSAGE = "Nincs elegendő termék.";

    public InvalidBookArgumentException(Product product, Integer quantity) {
        super(DEFAULT_MESSAGE+" Termék: "+product.getItemName()+", kívánt módosítás: "+quantity);
    }
}
