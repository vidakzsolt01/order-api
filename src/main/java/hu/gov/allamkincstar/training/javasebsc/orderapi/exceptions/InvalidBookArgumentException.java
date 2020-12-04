package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class InvalidBookArgumentException extends Exception{
    private static final String DEFAULT_MESSAGE = "Nincs elegendő mennyiség a termékből.";

    public InvalidBookArgumentException(Integer quantity) {
        super(DEFAULT_MESSAGE+" Kívánt módosítás: "+quantity);
    }
}
