package hu.gov.allamkincstar.training.javasebsc.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.interfaces.ContainerHandler;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NoItemFoundException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

import java.util.HashMap;
import java.util.Map;

public class Container implements ContainerHandler {

    protected final Map<String, Lot> containerItems;

    public Container() {
        containerItems = new HashMap<>();
    }

    public Container(Lot item) {
        this();
        containerItems.put(item.index, item);
    }

    @Override
    public void registerNewItem(Product product, int quantity) {
        if (containerItems.containsKey(product.itemNumber)){
            Lot itemInContainer = containerItems.get(product.itemNumber);
            itemInContainer.quantity += quantity;
        } else {
            Lot iteNew = new Lot(product, quantity);
            containerItems.put(iteNew.getIndex(), iteNew);
        }
    }

    @Override
    public void removeItem(String itemIndex) throws NoItemFoundException {
        findItem(itemIndex);
        containerItems.remove(itemIndex);
    }

    @Override
    public Lot findItem(String lotIndex) {
        if (!containerItems.containsKey(lotIndex)) throw new NoItemFoundException();
        return containerItems.get(lotIndex);
    }

    @Override
    public boolean isProductExist(String index) {
        return containerItems.containsKey(index);
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
