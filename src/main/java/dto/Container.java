package dto;

import Interfaces.ContainerHandler;
import exceptions.NoItemFounException;
import exceptions.NotEnoughItemException;
import order.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class Container implements ContainerHandler {

    protected final Map<String, Lot> containerItems;

    public Container() {
        containerItems = new HashMap<>();
    }

    public Container(Lot item) {
        this();
        containerItems.put(item.getProduct().getItemNumber(), item);
    }

    @Override
    public void registerNewItem(Lot item) {
        if (containerItems.containsKey(item.getProduct().getItemNumber())){
            Lot itemInOrder = containerItems.get(item.getProduct().getItemNumber());
            itemInOrder.quantity += item.getQuantity();
        } else {
            containerItems.put(item.getProduct().getItemNumber(), (OrderItem)item);
        }
    }

    @Override
    public void removeItem(Lot lot) throws NoItemFounException {
        if (!containerItems.containsKey(lot.getProduct().getItemNumber())) throw new NoItemFounException();
        containerItems.remove(lot.getProduct().getItemNumber());
    }

    @Override
    public void changeItemQuantity(Lot item, Integer quantity) throws NotEnoughItemException {
        item.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(Lot item) throws NoItemFounException {
        if (item.getQuantity() == 0) removeItem(item);
    }

}
