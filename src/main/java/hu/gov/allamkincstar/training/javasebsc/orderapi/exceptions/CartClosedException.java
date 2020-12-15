package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

public class CartClosedException extends Exception{

    private static final String MESSAGE = "A kosár lezárva";

    public CartClosedException(){
        super(MESSAGE);
    }
}
