package stock;

import baseclasses.Lot;
import baseclasses.Product;

import java.time.LocalDate;

public class StockItem extends Lot {

    private Integer totalSum;
    private LocalDate stockInDate;

    public StockItem(Product product, Integer quantity) {
        super(product, quantity);
        this.quantity = quantity;
        this.stockInDate = LocalDate.now();
        totalSum = product.getNetUntiPrice() * quantity;
    }

    public String getIndex(){
        return index;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
