package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;

public abstract class ProductItem {

    protected final Product product;
    private Integer quantity;
    protected final String index;

    public ProductItem(Product product, int quantity) {
        // Itt kellene vizsgálni, hogy a mennyiség ne lehessen 1-nél kisebb.
        // Elsőre InvalidQuantityArgumentException-t dobtam, de ez messzire vezet
        // (az OrderItem/StockItem minden példányosításánál kezelni kell a kivételt; brrrr!!!)
        // Másodikra azt gondoltam, hogy ilyenkor simán 1-et teszek a quantity-be, ám
        // a Unit tesztek rávilágítottak, hogy ez is szamárság: mikor lekérem
        // egy tároló tartalmát (pl. productItemList), akkor másolatot csinálok a listákról,
        // melyekben az elemeket újra példányosítom (new ...Item()), így soha nem
        // tudok olyan (valós!!!) listát produkálni, amelyben bármely elem mennyisége
        // 0 lenne.
        // Mindezekért a végső megoldás:
        // - a 0-t elfogadom valid értéknek
        // - a 0-nál kisebb értékekre azt mondom, hogy miután az API-ban a mennyiség-változtatások
        //   ellenőrzöttek, és nem eshet sehol negatívba, a negatív érték csak kliens programhiba
        //   lehet, ezért jó az InvalidQuantityArgumentException, de RuntimeException-ként.
        if (quantity < 0)throw new InvalidQuantityArgumentException();
        this.quantity = quantity;
        this.product = product;
        index = product.itemNumber;
    }

    public Product getProduct() {return product;    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getIndex() {
        return index;
    }

    public void increaseQuantity(int quantityToAdd) throws InvalidQuantityArgumentException {
        if (quantityToAdd < 0) throw  new InvalidQuantityArgumentException();
        quantity += quantityToAdd;
    }

    public void decreaseQuantity(int quantityToSubtract) throws NotEnoughItemException, InvalidQuantityArgumentException {
        if (quantityToSubtract < 0) throw  new InvalidQuantityArgumentException();
        if (this.quantity < quantityToSubtract) throw new NotEnoughItemException();
        quantity -= quantityToSubtract;
    }

}
