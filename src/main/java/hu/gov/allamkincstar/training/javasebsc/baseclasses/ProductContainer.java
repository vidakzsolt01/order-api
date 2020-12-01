package hu.gov.allamkincstar.training.javasebsc.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.interfaces.ProductContainerHandler;

import java.util.HashMap;
import java.util.Map;

public class ProductContainer implements ProductContainerHandler {

    private final Map<String, Lot> productItems;

    private final Map<String, String> controlHeap = new HashMap<>();

    public ProductContainer() {
        productItems = new HashMap<>();
    }

    public ProductContainer(Lot productItem) {
        this();
        this.productItems.put(productItem.index, productItem);
        this.controlHeap.put(productItem.product.getItemName(), productItem.product.itemNumber);
    }

    public Map<String, Lot> getProductItems() {
        return productItems;
    }

    @Override
    public void registerNewItem(Lot itemToStore) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
        if (productItems.containsKey(itemToStore.index)){
            Lot itemInContainer = productItems.get(itemToStore.index);
            checkItemInContaner(itemInContainer, itemToStore);
            itemInContainer.quantity += itemToStore.quantity;
        } else {
            productItems.put(itemToStore.index, itemToStore);
        }
    }

    private void checkItemInContaner(Lot itemInContainer, Lot itemToStore) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
        // If product exists with this item number and it's name not match with the new one's
        if (!itemInContainer.getProduct().itemName.equals(itemToStore.product.itemName))
            throw new ItemExistsWithItemNumberException(itemInContainer.product);
        // if any product already exists with new one's name
        if (controlHeap.containsKey(itemToStore.product.getItemName())){
            String itemNumber = controlHeap.get(itemToStore.product.getItemName());
            if (!itemNumber.equals(itemInContainer.product.itemNumber)){
                throw new ItemExistsWithNameException(itemInContainer.product);
            }
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
