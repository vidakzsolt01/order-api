package dto;

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
}
