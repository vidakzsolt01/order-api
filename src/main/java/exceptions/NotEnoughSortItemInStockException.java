package exceptions;

import dto.order.OrderItem;
import dto.stock.StockItem;

public class NotEnoughSortItemInStockException extends Exception{
    private static final String DEFAULT_MESSAGE = "Nincs elegendő a mennyiség raktáron a termékből.";

    public NotEnoughSortItemInStockException(StockItem item, Integer quantity) {
        this(DEFAULT_MESSAGE, item, quantity);
    }

    public NotEnoughSortItemInStockException(String message, StockItem item, Integer quantity) {
        super(message+" Termék: "+item.getProduct().getItemName()+", kért mennyiség: "+quantity);
    }
}
