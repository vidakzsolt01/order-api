package dto.stock;

import dto.Lot;
import dto.Product;

import java.time.LocalDate;

public class StockItem extends Lot {

    private LocalDate stockInDate;

    public StockItem(Product product, Integer quantity) {
        super(product, quantity);
        this.quantity = quantity;
        this.stockInDate = LocalDate.now();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
