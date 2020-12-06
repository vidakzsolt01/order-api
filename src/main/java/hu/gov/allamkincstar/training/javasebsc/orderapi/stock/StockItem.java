package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidIncreaseArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public class StockItem extends ProductItem{

    private Integer bookedQuantity;

    StockItem(Product product, int quantity){
        super(product, quantity);
        this.bookedQuantity = 0;
    }

    public StockItem(ProductItem value){
        super(value.getProduct(), value.getQuantity());
    }

    public StockItem bookSomeQuantity(int quantityToBook) throws NotEnoughItemException{
        if (!isBookable(quantityToBook))
            throw new NotEnoughItemException("Nem foglalható le a kívánt mennyiség a termékből", this, quantityToBook);
        bookedQuantity += quantityToBook;
        return this;
    }

    public void releaseBookedQuantity(int quantityToRelease){
        bookedQuantity -= quantityToRelease;
    }

    private StockItem expendBookedQuantity(int quantityToExpend) throws NotEnoughItemException, InvalidIncreaseArgumentException{
        decreaseQuantity(quantityToExpend);
        return this;
    }

    private boolean isBookable(int quantityToBook){
        return (getBookableQuantity() >= quantityToBook);
    }

    public Integer getBookedQuantity(){
        return bookedQuantity;
    }

    int getBookableQuantity(){
        return getQuantity() - bookedQuantity;
    }

    public void finishBook(int quantityToFinish) throws NotEnoughItemException, InvalidIncreaseArgumentException{
        if (quantityToFinish > getQuantity())
            throw new NotEnoughItemException("Nincs elég mennyiség a raktárkészlet kívánt véglegesítéshez", this, getQuantity());
        if (quantityToFinish > bookedQuantity)
            throw new NotEnoughItemException("Nincs akkora lefoglalt mennyiség ami a raktárkészlet véglegesítéshez kellene", this, getQuantity());
        decreaseQuantity(quantityToFinish);
        bookedQuantity -= quantityToFinish;
    }
}
