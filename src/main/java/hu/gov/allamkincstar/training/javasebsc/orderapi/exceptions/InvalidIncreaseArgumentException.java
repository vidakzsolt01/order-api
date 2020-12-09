package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

/**
 * hibás program okozhatja: RuntimeException lesz
 */
public class InvalidIncreaseArgumentException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Érvénytelen érték a mennyiség növeléséhez";

    public InvalidIncreaseArgumentException(int quantity) {
        super(DEFAULT_MESSAGE+" : "+quantity);
    }

}
