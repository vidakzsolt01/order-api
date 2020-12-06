package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.interfaces.ProductContainerHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ProductContainer implements ProductContainerHandler {

    protected final Map<String, ProductItem> productItems;
    private final Map<String, String>        controlHeapByName = new HashMap<>();

    public ProductContainer(){
        productItems = new HashMap<>();
    }

    public ProductContainer(ProductItem productItem) {
        this();
        putItemToContainer(productItem);
    }

    private void putItemToContainer(ProductItem productItem){
        productItems.put(productItem.index, productItem);
        controlHeapByName.put(productItem.product.getItemName(), productItem.product.itemNumber);
    }

    @Override
    public void registerNewItem(ProductItem itemToStore) throws ItemExistsWithNameException, ItemExistsWithItemNumberException, InvalidIncreaseArgumentException {
        if (productItems.containsKey(itemToStore.index)){
            ProductItem itemInContainer = productItems.get(itemToStore.index);
            checkItemInContaner(itemInContainer, itemToStore);
            itemInContainer.increaseQuantity(itemToStore.getQuantity());
        } else {
            checkItemInContaner(itemToStore, itemToStore);
            putItemToContainer(itemToStore);
        }
    }

    @Override
    public void addSomeMoreQuantity(String itemNumber, int quantity) throws InvalidIncreaseArgumentException {
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
        if (controlHeapByName.containsKey(itemToStore.product.getItemName())){
            String itemNumber = controlHeapByName.get(itemToStore.product.getItemName());
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
    public void changeItemQuantity(String itemNumber, int quantity) throws NotEnoughItemException, InvalidIncreaseArgumentException {
        ProductItem foundItem = findItem(itemNumber);
        if (quantity < 0) foundItem.decreaseQuantity(quantity);
        else foundItem.increaseQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(ProductItem item) throws NoItemFoundException, NotEmptyItemException {
        ProductItem foundItem = findItem(item.index);
        if (foundItem.getQuantity() != 0) throw new NotEmptyItemException(item);
        removeItem(item.index);
    }

    /**
     * A productItems közvetlen "külső" hozzáférését le akarom tiltani (tehát
     * még getter-t sem adok hozzá), viszont a benne lévó tételeket látni kell engedni.
     * Ezért ez a metódus egy olyan listát ad vissza, amely az eredeti ProductItem-ek
     * <i>másolatát</i> tartalmazza csupán.
     * Miután
     *
     * @return a
     */
    @Override
    public abstract List<ProductItem> productItemList();

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
