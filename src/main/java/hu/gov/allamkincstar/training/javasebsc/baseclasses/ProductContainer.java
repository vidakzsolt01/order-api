package hu.gov.allamkincstar.training.javasebsc.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.interfaces.ProductContainerHandler;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

import java.util.HashMap;
import java.util.Map;

public class ProductContainer implements ProductContainerHandler {

    protected final Map<String, Lot> productItems;

    public ProductContainer() {
        productItems = new HashMap<>();
    }

    public ProductContainer(Lot item) {
        this();
        productItems.put(item.index, item);
    }

    @Override
    public void registerNewItem(Lot item) {
        if (productItems.containsKey(item.index)){
            Lot itemInContainer = productItems.get(item.index);
            itemInContainer.quantity += item.quantity;
        } else {
            productItems.put(item.index, item);
        }
    }

    @Override
    public void removeItem(String itemIndex) throws NoItemFoundException {
        findItem(itemIndex);
        productItems.remove(itemIndex);
    }

    @Override
    public Lot findItem(String lotIndex) {
        if (!productItems.containsKey(lotIndex)) throw new NoItemFoundException();
        return productItems.get(lotIndex);
    }

    @Override
    public boolean isProductExist(String index) {
        return productItems.containsKey(index);
    }

    @Override
    public void changeItemQuantity(String itemIndex, Integer quantity) throws NotEnoughItemException {
        Lot foundItem = findItem(itemIndex);
        foundItem.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(String itemIndex) throws NoItemFoundException {
        Lot foundItem = findItem(itemIndex);
        if (foundItem.getQuantity() == 0) removeItem(itemIndex);
    }

}
