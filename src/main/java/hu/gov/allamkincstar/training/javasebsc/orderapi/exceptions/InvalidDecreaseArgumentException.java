package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

/**
 * hibás program okozhatja: RuntimeException lesz
 */
public class InvalidDecreaseArgumentException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "Érvénytelen érték a mennyiség csökkentéséhez";

    public InvalidDecreaseArgumentException(int quantity) {
        super(DEFAULT_MESSAGE+" : "+quantity);
    }
}
