package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public abstract class ProductItem {

    protected final Product product;
    protected Integer quantity;
    protected final String index;

    public ProductItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        index = product.itemNumber;
    }

    public Product getProduct() {return product;    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getIndex() {
        return index;
    }

    public void changeItemQuantity(Integer chageQuantity) throws NotEnoughItemException {
        if (chageQuantity == null || chageQuantity == 0) return;
        if (this.quantity + chageQuantity < 0){
            throw new NotEnoughItemException(this, chageQuantity);
        }
        quantity += chageQuantity;
    }

    public void increaseQuantity(int quantity){
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) throws NotEnoughItemException {
        if (this.quantity < quantity) throw new NotEnoughItemException(this, quantity);
    }
}
