package hu.gov.allamkincstar.training.javasebsc.stock;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;
import hu.gov.allamkincstar.training.javasebsc.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.order.OrderItem;

import java.util.Map;

public class Stock extends ProductContainer {

    public Stock() {
        super();
    }

    public Stock(Product product, int quantity) {
        super();
        registerNewItem(new StockItem(product, quantity));
    }

    public OrderItem bookProduct(Product product, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        if (quantity <= 0) throw new InvalidBookArgumentException(product, quantity);
        StockItem stockItem = (StockItem) findItem(product.getItemNumber());
        return  new OrderItem(stockItem.bookSomeQuantity(quantity), quantity);
    }

    public void depositProduct(Product product, int quantity){
        registerNewItem(new StockItem(product, quantity));

    }

    public int getBookedQuantity(Product product){
        StockItem stockItem = (StockItem) findItem(product.getItemNumber());
        return stockItem.bookedQuantity;
    }

    public int getBookableQuantity(Product product){
        StockItem stockItem = (StockItem) findItem(product.getItemNumber());
        return stockItem.getBookableQuantity();
    }

    private class StockItem extends Lot {

        private Integer bookedQuantity;

        private StockItem(Product product, int quantity) {
            super(product, quantity);
            this.bookedQuantity = 0;
        }

        private StockItem bookSomeQuantity(int quantityToBook) throws NotEnoughItemException {
            if (!isBookable(quantityToBook))
                throw new NotEnoughItemException("Nem foglalható le a kívánt mennyiség a termékből", this, quantityToBook);
            bookedQuantity += quantityToBook;
            return this;
        }

        private StockItem releaseBookedQuantity(int quantityToRelease){
            bookedQuantity -= quantityToRelease;
            return this;
        }

        private StockItem expendBookedQuantity(int quantityToExpend){
            quantity -= quantityToExpend;
            return this;
        }

        private boolean isBookable(int quantityToBook){
            return (getBookableQuantity() >= quantityToBook);
        }

        private int getBookableQuantity(){
            return quantity - bookedQuantity;
        }
    }
}
