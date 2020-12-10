package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public abstract class ProductItem {

    protected final Product product;
    private Integer quantity;
    protected final String index;

    public ProductItem(Product product, int quantity) {
        // Itt kell vizsgálni, hogy a mennyiség ne lehessen 1-nél kisebb.
        // Elsőre InvalidQuantityArgumentExceptiont-t dobtam, de ez messzire vezet
        // (az Ordeitem/Stockitem minden példányosításánál kezelni kell a kivételt),
        // ezért elvetettem (nem olyan "komoly" program ez most) és ilyenkor simán
        // 1-et teszek a quantity-be.
        if (quantity <= 0) this.quantity = 1;
        else this.quantity = quantity;
        this.product = product;
        index = product.itemNumber;
    }

    public Product getProduct() {return product;    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getIndex() {
        return index;
    }

    public void increaseQuantity(int quantityToAdd) throws InvalidQuantityArgumentException {
        if (quantityToAdd < 0) throw  new InvalidQuantityArgumentException();
        quantity += quantityToAdd;
    }

    public void decreaseQuantity(int quantityToSubtract) throws NotEnoughItemException, InvalidQuantityArgumentException {
        if (quantityToSubtract < 0) throw  new InvalidQuantityArgumentException();
        if (this.quantity < quantityToSubtract) throw new NotEnoughItemException();
        quantity -= quantityToSubtract;
    }

}
