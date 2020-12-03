package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidBookArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.ItemExistsWithItemNumberException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.ItemExistsWithNameException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class Stock extends ProductContainer {

    private Map<String, Integer> controlHeap = new HashMap<>();

    public Stock() {
        super();
    }

    public Stock(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
        super();
        registerNewItem(new StockItem(product, quantity));
    }

    public OrderItem bookProduct(Product product, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        if (quantity <= 0) throw new InvalidBookArgumentException(product, quantity);
        StockItem stockItem = (StockItem) findItem(product.getItemNumber());
        return  new OrderItem(stockItem.bookSomeQuantity(quantity), quantity);
    }

    public void depositProduct(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
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

    public void finishBook(Product product, int quantity) throws NotEnoughItemException {
        StockItem stockItem = (StockItem) findItem(product.getItemNumber());
        stockItem.finishBook(quantity);
    }

    private class StockItem extends ProductItem {

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

        private void  finishBook(int quantityToFinish) throws NotEnoughItemException {
            if (quantityToFinish > quantity)
                throw new NotEnoughItemException("Nincs elég mennyiség a raktárkészlet kívánt véglegesítéshez", this, quantity);
            if (quantityToFinish > bookedQuantity)
                throw new NotEnoughItemException("Nincs akkora lefoglalt mennyiség ami a raktárkészlet véglegesítéshez kellene", this, quantity);
            quantity -= quantityToFinish;
            bookedQuantity -= quantityToFinish;
        }
    }
}
