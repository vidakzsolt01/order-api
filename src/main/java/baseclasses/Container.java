package baseclasses;

import Interfaces.ContainerHandler;
import exceptions.NoItemFounException;
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
    public void removeItem(Lot lot) throws NoItemFounException {
        findItem(lot);
        containerItems.remove(lot.index);
    }

    @Override
    public void findItem(Lot lot) {
        if (!containerItems.containsKey(lot.index)) throw new NoItemFounException();
    }

    @Override
    public void changeItemQuantity(Lot item, Integer quantity) throws NotEnoughItemException {
        findItem(item);
        item.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(Lot item) throws NoItemFounException {
        findItem(item);
        if (item.getQuantity() == 0) removeItem(item);
    }

}
