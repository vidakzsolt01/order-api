package dto;

import exceptions.NotEnoughItemException;
import order.OrderItem;

public class Lot{

    protected final Product product;
    protected Integer quantity;
    protected final String index;

    public Lot(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        index = product.itemNumber;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void changeItemQuantity(Integer quantity) throws NotEnoughItemException {
        if (quantity == null || quantity == 0) return;
        if (this.quantity + quantity < 0){
            throw new NotEnoughItemException(this, quantity);
        }
        this.quantity += quantity;
    }

}
