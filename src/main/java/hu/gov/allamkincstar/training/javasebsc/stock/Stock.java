package hu.gov.allamkincstar.training.javasebsc.stock;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.order.OrderItem;

import java.util.Map;

public class Stock extends ProductContainer {

    public Stock() {
        super();
    }

    public Stock(Lot item) {
        super(item);
    }

    public OrderItem bookProduct(Product product, int quantity) throws NotEnoughItemException {
        StockItem item = (StockItem) findItem(product.getItemNumber());
        return  new OrderItem(item.bookSomeQuantity(quantity), quantity);
    }

    public void depositProduct(Product product, int quantity){
        registerNewItem(product, quantity);
    }


    public Map<String, Lot> getItems(){
        return productItems;
    }

    private class StockItem extends Lot {

        private Integer bookedQuantity;

        private StockItem(Product product, Integer quantity) {
            super(product, quantity);
        }

        private StockItem bookSomeQuantity(Integer quantityToBook) throws NotEnoughItemException {
            if (!isBookable(quantityToBook))
                throw new NotEnoughItemException("Nem foglalható le a kívánt mennyiség a termékből", this, quantityToBook);
            bookedQuantity += quantityToBook;
            return this;
        }

        private StockItem releaseBookedQuantity(Integer quantityToRelease){
            bookedQuantity -= quantityToRelease;
            return this;
        }

        private StockItem expendBookedQuantity(Integer quantityToExpend){
            quantity -= quantityToExpend;
            return this;
        }

        private boolean isBookable(Integer quantityToBook){
            return (getBookableQuantity() >= quantityToBook);
        }

        private int getBookableQuantity(){
            return quantity - bookedQuantity;
        }

/*
        private String getIndex(){
            return index;
        }

        public Product getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        private void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
*/

    }
}
