package dto;

import exceptions.NotEnoughItemException;
import order.OrderItem;

public class Lot{

    protected Product product;
    protected Integer quantity;

    public Lot(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
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
