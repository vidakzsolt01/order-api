package dto.order;

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

}
