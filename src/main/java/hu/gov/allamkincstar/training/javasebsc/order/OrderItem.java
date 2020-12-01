package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;

public class OrderItem extends Lot {

    private final Integer netAmount;
    private final Integer VATAmount;

    public OrderItem(Product product, Integer quantity) {
        super(product, quantity);
        Float VAT = Float.valueOf(product.getVATPercent()) / 100;
        netAmount = product.getNetUntiPrice() * quantity;
        VATAmount = Math.round(netAmount * VAT);
    }

    public OrderItem(Lot lot) {
        this(lot.getProduct(), lot.getQuantity());
    }

    public OrderItem(Lot item, Integer quantity) {
        this(item.getProduct(), quantity);
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

}
