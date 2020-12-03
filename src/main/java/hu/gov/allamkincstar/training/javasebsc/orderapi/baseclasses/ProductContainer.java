package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.interfaces.ProductContainerHandler;

import java.util.HashMap;
import java.util.Map;

public class ProductContainer implements ProductContainerHandler {

    private final Map<String, ProductItem> productItems;

    private final Map<String, String> controlHeap = new HashMap<>();

    public ProductContainer() {
        productItems = new HashMap<>();
    }

    public ProductContainer(ProductItem productItem) {
        this();
        putItemToContainer(productItem);
    }

    private void putItemToContainer(ProductItem productItem){
        productItems.put(productItem.index, productItem);
        controlHeap.put(productItem.product.getItemName(), productItem.product.itemNumber);
    }

    public ProductItem getItem(String itemNumber){
        return searchItem(itemNumber);
    }

    public Map<String, ProductItem> getProductItems() {
        return productItems;
    }

    @Override
    public void registerNewItem(ProductItem itemToStore) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
        if (productItems.containsKey(itemToStore.index)){
            ProductItem itemInContainer = productItems.get(itemToStore.index);
            checkItemInContaner(itemInContainer, itemToStore);
            itemInContainer.quantity += itemToStore.quantity;
        } else {
            putItemToContainer(itemToStore);
        }
    }

    @Override
    public void addItem(String itemNumber, int quantity) {
        ProductItem item;
        if ((item = searchItem(itemNumber)) == null){
            throw new NoItemFoundException();
        }
        item.increaseQuantity(quantity);
    }

    private void checkItemInContaner(ProductItem itemInContainer, ProductItem itemToStore) throws ItemExistsWithNameException, ItemExistsWithItemNumberException {
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
    public void removeItem(String itemNumber) {
        findItem(itemNumber);
        productItems.remove(itemNumber);
    }

    @Override
    public ProductItem findItem(String itemNumber) {
        if (!productItems.containsKey(itemNumber)) throw new NoItemFoundException();
        return productItems.get(itemNumber);
    }

    @Override
    public ProductItem searchItem(String itemNumber) {
        return productItems.get(itemNumber);
    }

    @Override
    public void changeItemQuantity(String itemIndex, Integer quantity) throws NotEnoughItemException {
        ProductItem foundItem = findItem(itemIndex);
        foundItem.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(ProductItem item) throws NoItemFoundException, NotEmptyItemException {
        ProductItem foundItem = findItem(item.index);
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

    public boolean isProductExist(ProductItem item) {
        return productItems.containsKey(item.index);
    }

}
