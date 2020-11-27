package exceptions;

import dto.Product;

public class NoStockItemFoundInStockException extends Exception{
    private static final String DEFAULT_MESSAGE = "A keresett termék nem található a raktárban.";

    public NoStockItemFoundInStockException(Product product) {
        this(DEFAULT_MESSAGE, product);
    }

    public NoStockItemFoundInStockException(String message, Product product) {
        super(message+" Termék: "+product.getItemName());
    }
}
