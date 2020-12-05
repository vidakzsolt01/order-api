package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductContainer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public final class Stock extends ProductContainer {

    public Stock() {
        super();
    }

    @Override
    public List<ProductItem> productItemList(){
        List<ProductItem> itemList = new ArrayList<>();
        productItems.forEach((key, value) -> itemList.add(new StockItem(value)));
        return itemList;
    }

    public Stock(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        super();
        registerNewItem(new StockItem(product, quantity));
    }

    public void depositProduct(Product product, int quantity) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        registerNewItem(new StockItem(product, quantity));
    }

    public OrderItem bookProduct(String itemNumber, int quantity) throws NotEnoughItemException, InvalidBookArgumentException {
        if (quantity <= 0) throw new InvalidBookArgumentException(quantity);
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return  new OrderItem(stockItem.bookSomeQuantity(quantity), quantity);
    }

    public void releaseBookedQuantity(String itemNumber, int quantityToRelease){
        ((StockItem) findItem(itemNumber)).releaseBookedQuantity(quantityToRelease);
    }

    public int getBookedQuantity(String itemNumber){
        return ((StockItem) findItem(itemNumber)).bookedQuantity;
    }

    public int getBookableQuantity(String itemNumber){
        StockItem stockItem = (StockItem) findItem(itemNumber);
        return stockItem.getBookableQuantity();
    }

    public void finishItemBook(String itemNumber, int quantity) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        ((StockItem) findItem(itemNumber)).finishBook(quantity);
    }

    private static class StockItem extends ProductItem {

        private Integer bookedQuantity;

        private StockItem(Product product, int quantity) {
            super(product, quantity);
            this.bookedQuantity = 0;
        }

        public StockItem(ProductItem value){
            super(value.getProduct(), value.getQuantity());
        }

        private StockItem bookSomeQuantity(int quantityToBook) throws NotEnoughItemException {
            if (!isBookable(quantityToBook))
                throw new NotEnoughItemException("Nem foglalható le a kívánt mennyiség a termékből", this, quantityToBook);
            bookedQuantity += quantityToBook;
            return this;
        }

        private void releaseBookedQuantity(int quantityToRelease){
            bookedQuantity -= quantityToRelease;
        }

        private StockItem expendBookedQuantity(int quantityToExpend) throws NotEnoughItemException, InvalidIncreaseArgumentException {
            decreaseQuantity(quantityToExpend);
            return this;
        }

        private boolean isBookable(int quantityToBook){
            return (getBookableQuantity() >= quantityToBook);
        }

        private int getBookableQuantity(){
            return getQuantity() - bookedQuantity;
        }

        private void  finishBook(int quantityToFinish) throws NotEnoughItemException, InvalidIncreaseArgumentException {
            if (quantityToFinish > getQuantity())
                throw new NotEnoughItemException("Nincs elég mennyiség a raktárkészlet kívánt véglegesítéshez", this, getQuantity());
            if (quantityToFinish > bookedQuantity)
                throw new NotEnoughItemException("Nincs akkora lefoglalt mennyiség ami a raktárkészlet véglegesítéshez kellene", this, getQuantity());
            decreaseQuantity(quantityToFinish);
            bookedQuantity -= quantityToFinish;
        }
    }
}
