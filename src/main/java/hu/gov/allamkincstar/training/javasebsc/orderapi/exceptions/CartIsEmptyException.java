package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

public class CartIsEmptyException extends Exception{

    public CartIsEmptyException() {
        super("Üres a kosár!");
    }
}
