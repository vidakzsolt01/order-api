package exceptions;

import dto.order.OrderItem;

public class NoOrderItemFounInOrdersException extends Exception{

    private static final String DEFAULT_MESSAGE = "A keresett termék nem található a megrendelés tételei közt.";

    public NoOrderItemFounInOrdersException() {
        super(DEFAULT_MESSAGE);
    }

    public NoOrderItemFounInOrdersException(String message, OrderItem item) {
        super(message+" Termék: "+item.getProduct().getItemName());
    }

    public NoOrderItemFounInOrdersException(OrderItem item) {
        this(DEFAULT_MESSAGE, item);
    }

}
