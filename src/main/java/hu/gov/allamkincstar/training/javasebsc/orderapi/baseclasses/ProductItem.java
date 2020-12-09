package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidIncreaseArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public abstract class ProductItem {

    protected final Product product;
    private Integer quantity;
    protected final String index;

    public ProductItem(Product product, int quantity) {
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

    public void increaseQuantity(int quantityToAdd) throws InvalidIncreaseArgumentException {
        if (quantityToAdd < 0) throw  new InvalidIncreaseArgumentException(quantityToAdd);
        quantity += quantityToAdd;
    }

    public void decreaseQuantity(int quantityToSubtract) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        if (quantityToSubtract < 0) throw  new InvalidIncreaseArgumentException(quantityToSubtract);
        if (this.quantity < quantityToSubtract) throw new NotEnoughItemException(this, quantityToSubtract);
        quantity -= quantityToSubtract;
    }

}
