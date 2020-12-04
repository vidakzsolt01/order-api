package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

public class InvalidDecreaseArgumentException extends Exception{
    private static final String DEFAULT_MESSAGE = "Érvénytelen érték a mennyiség csökkentéséhez";

    public InvalidDecreaseArgumentException(int quantity) {
        super(DEFAULT_MESSAGE+" : "+quantity);
    }
}
