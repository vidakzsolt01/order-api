package stock;

import baseclasses.Lot;
import baseclasses.Product;
import exceptions.NotEnoughItemException;

import java.time.LocalDate;

/**
 * Itt kellene kezelni pl. a tételek lefoglalását,
 * felszabadítását, de a raktárkezeléssel összefüggő
 * funkciók nem része a feladatnak. Csupán
 * azt kell tudnunk, hogy egy adott termékből
 * van-e annyi, amennyit rendel az Order class,
 * ezt pedig a szülő is "tudja". Így maradnak a szülő
 * property-jeinek getterei...
 */
public class StockItem extends Lot {

/*
    private Integer bookedQuantity;
*/

    public StockItem(Product product, Integer quantity) {
        super(product, quantity);
    }

/*
    public Integer getBookedQuantity() {
        return bookedQuantity;
    }

    public StockItem book(Integer quantityToBook) throws NotEnoughItemException {
        if (quantity < bookedQuantity + quantityToBook)
            throw new NotEnoughItemException("Nem foglalható le ennyi termék", this, quantityToBook);
        bookedQuantity += quantityToBook;
        return this;
    }

    public StockItem release(Integer quantityToBook){
        bookedQuantity -= quantityToBook;
        return this;
    }
*/

    public String getIndex(){
        return index;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
