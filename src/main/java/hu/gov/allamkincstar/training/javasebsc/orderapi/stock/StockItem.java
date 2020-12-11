package hu.gov.allamkincstar.training.javasebsc.orderapi.stock;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.Product;
import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public class StockItem extends ProductItem{

    private Integer bookedQuantity;

    StockItem(Product product, int quantity){
        super(product, quantity);
        this.bookedQuantity = 0;
    }

    public StockItem(StockItem productItem){
        super(productItem.getProduct(), productItem.getQuantity());
        this.bookedQuantity = productItem.bookedQuantity;
    }

    public StockItem bookSomeQuantity(int quantityToBook) throws NotEnoughItemException{
        if (!isBookableQuantity(quantityToBook))
            throw new NotEnoughItemException("Nem foglalható le a kívánt mennyiség a termékből");
        bookedQuantity += quantityToBook;
        return this;
    }

    public void releaseBookedQuantity(int quantityToRelease) throws NotEnoughItemException {
        if (quantityToRelease > bookedQuantity) throw new NotEnoughItemException("A felszabadítandó mennyiség nem lehet több a foglaltnál");
        bookedQuantity -= quantityToRelease;
    }

    private StockItem expendBookedQuantity(int quantityToExpend) throws NotEnoughItemException, InvalidQuantityArgumentException {
        decreaseQuantity(quantityToExpend);
        return this;
    }

    public boolean isBookableQuantity(int quantityToBook){
        return (getBookableQuantity() >= quantityToBook);
    }

    public boolean isBookable(){
        return (getBookableQuantity() > 0);
    }

    public Integer getBookedQuantity(){
        return bookedQuantity;
    }

    int getBookableQuantity(){
        return getQuantity() - bookedQuantity;
    }

    public void finishBook(int quantityToFinish) throws NotEnoughItemException, InvalidQuantityArgumentException {
        // ezt hagyjuk: mindegy, hogy mennyi az összes (úgysem lehet kecesebb a foglaltnál),
        // elég csak a foglalt mennyiségre koncentrálni.
        //if (quantityToFinish > getQuantity())
        //    throw new NotEnoughItemException("Nincs elég mennyiség a raktárkészlet kívánt véglegesítéshez");
        if (quantityToFinish > bookedQuantity)
            throw new NotEnoughItemException("A véglegesíteni kívánt mennyiség nincs lefoglalva");
        decreaseQuantity(quantityToFinish);
        bookedQuantity -= quantityToFinish;
    }
}
