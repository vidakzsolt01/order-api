package hu.gov.allamkincstar.training.javasebsc.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEmptyItemException;
import hu.gov.allamkincstar.training.javasebsc.interfaces.ProductContainerHandler;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

import java.util.HashMap;
import java.util.Map;

public class ProductContainer implements ProductContainerHandler {

    private final Map<String, Lot> productItems;

    public ProductContainer() {
        productItems = new HashMap<>();
    }

    public ProductContainer(Lot productItem) {
        this();
        this.productItems.put(productItem.index, productItem);
    }

    public Map<String, Lot> getProductItems() {
        return productItems;
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
    public void removeItem(String itemIndex) {
        findItem(itemIndex);
        productItems.remove(itemIndex);
    }

    @Override
    public Lot findItem(String lotIndex) {
        if (!productItems.containsKey(lotIndex)) throw new NoItemFoundException();
        return productItems.get(lotIndex);
    }

    @Override
    public void changeItemQuantity(String itemIndex, Integer quantity) throws NotEnoughItemException {
        Lot foundItem = findItem(itemIndex);
        foundItem.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(Lot item) throws NoItemFoundException, NotEmptyItemException {
        Lot foundItem = findItem(item.index);
        if (foundItem.getQuantity() != 0) throw new NotEmptyItemException(item);
        removeItem(item.index);
    }

    @Override
    public boolean isProductExist(Product product) {
        return productItems.containsKey(product.getItemNumber());
    }

    public boolean isProductExist(String itemNumber) {
        return productItems.containsKey(itemNumber);
    }

    public boolean isProductExist(Lot item) {
        return productItems.containsKey(item.index);
    }

}
