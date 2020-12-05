package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;

public class OrderItem extends ProductItem {

    private final Integer netAmount;
    private final Integer VATAmount;

    public OrderItem(Product product, Integer quantity) {
        super(product, quantity);
        Float VAT = Float.valueOf(product.getVATPercent()) / 100;
        netAmount = product.getNetUntiPrice() * quantity;
        VATAmount = Math.round(netAmount * VAT);
    }

    public OrderItem(ProductItem productItem) {
        this(productItem.getProduct(), productItem.getQuantity());
    }

    public OrderItem(ProductItem item, Integer quantity) {
        this(item.getProduct(), quantity);
    }

    public String getIndex(){
        return index;
    }

    public Product getProduct(){
        return product;
    }

    public Integer getNetAmount() {
        return netAmount;
    }

    public Integer getVATAmount() {
        return VATAmount;
    }

}
