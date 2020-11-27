package exceptions;

import dto.order.OrderItem;

public class NotEnoughOrderItemQuantityException extends Exception{

    private static final String DEFAULT_MESSAGE = "Nincs elegendő termék a mennyiség módosításához.";

    public NotEnoughOrderItemQuantityException(OrderItem item, Integer quantity) {
        this(DEFAULT_MESSAGE, item, quantity);
    }

    public NotEnoughOrderItemQuantityException(String message, OrderItem item, Integer quantity) {
        super(message+" Termék: "+item.getProduct().getItemName()+", kívánt módosítás: "+quantity);
    }
}
