package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

public class IllegalOrderCreationException extends RuntimeException{
    public IllegalOrderCreationException() {
        super("Rendelés létrehozása nem legális módon");
    }
}
