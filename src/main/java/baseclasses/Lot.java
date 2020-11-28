package baseclasses;

import exceptions.NotEnoughItemException;

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

    public void changeItemQuantity(Integer chageQuantity) throws NotEnoughItemException {
        if (chageQuantity == null || chageQuantity == 0) return;
        if (this.quantity + chageQuantity < 0){
            throw new NotEnoughItemException(this, chageQuantity);
        }
        quantity += chageQuantity;
    }

}
