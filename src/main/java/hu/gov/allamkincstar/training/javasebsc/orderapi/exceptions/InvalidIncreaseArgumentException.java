package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class InvalidIncreaseArgumentException extends Exception{
    private static final String DEFAULT_MESSAGE = "Érvénytelen érték a mennyiség növeléséhez";

    public InvalidIncreaseArgumentException(int quantity) {
        super(DEFAULT_MESSAGE+" : "+quantity);
    }

}
