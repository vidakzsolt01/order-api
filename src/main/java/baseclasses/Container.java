package baseclasses;

import Interfaces.ContainerHandler;
import exceptions.NoItemFoundException;
import exceptions.NotEnoughItemException;

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
    public void registerNewItem(Lot item) {
        if (containerItems.containsKey(item.index)){
            Lot itemInContainer = containerItems.get(item.index);
            itemInContainer.quantity += item.getQuantity();
        } else {
            containerItems.put(item.index, item);
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
