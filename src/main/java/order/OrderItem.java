package order;

import dto.Lot;
import dto.Product;

public class OrderItem extends Lot {

    private final Integer netAmount;
    private final Integer VATAmount;
    private final Integer grossAmount;

    public OrderItem(Product product, Integer quantity) {
        super(product, quantity);
        Float VAT = Float.valueOf(product.getVATPercent()) / 100;
        netAmount = product.getNetUntiPrice() * quantity;
        VATAmount = Math.round(netAmount * VAT);
        grossAmount = netAmount + VATAmount;
    }

    public String getIndex(){
        return index;
    }

    public Product getProduct(){
        return product;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void  setQuantity(Integer quantity){
        super.quantity = quantity;
    }

    public Integer getNetAmount() {
        return netAmount;
    }

    public Integer getVATAmount() {
        return VATAmount;
    }

    public Integer getGrossAmount() {
        return grossAmount;
    }
}
