package order;

import exceptions.NoOrderItemFounInOrdersException;
import exceptions.NotEnoughOrderItemQuantityException;

import java.util.HashMap;
import java.util.Map;

public class Order {

    private Map<String, OrderItem> items;

    public Order() {
        items = new HashMap<>();
    }

    public Order(OrderItem item) {
        this();
        items.put(item.getProduct().getItemNumber(), item);
    }

    public Map<String, OrderItem> addItem(OrderItem item){
        if (items.containsKey(item.getProduct().getItemNumber())){
            OrderItem itemInOrder = items.get(item.getProduct().getItemNumber());
            itemInOrder.setQuantity(itemInOrder.getQuantity() + item.getQuantity());
            return items;
        }
        items.put(item.getProduct().getItemNumber(), item);
        return items;
    }

    public Map<String, OrderItem> increaseItemQuantity(OrderItem item) throws NoOrderItemFounInOrdersException {
        if (items.get(item.getProduct().getItemNumber()) != item){
            throw new NoOrderItemFounInOrdersException(item);
        }
        item.setQuantity(item.getQuantity()+1);
        return items;
    }

    public Map<String, OrderItem> decreaseItemQuantity(OrderItem item) throws NoOrderItemFounInOrdersException {
        if (items.get(item.getProduct().getItemNumber()) != item){
            throw new NoOrderItemFounInOrdersException(item);
        }
        item.setQuantity(item.getQuantity()-1);
        zeroQuantityCheck(item);
        return items;
    }

    private void changeItemQuantity(OrderItem item, Integer quantity) throws NotEnoughOrderItemQuantityException {
        if (quantity == null || quantity == 0) return;
        if (item.getQuantity() + quantity < 0){
            throw new NotEnoughOrderItemQuantityException(item, quantity);
        }
        zeroQuantityCheck(item);
    }

    private void zeroQuantityCheck(OrderItem item){
        if (item.getQuantity() == 0){
            items.remove(item.getProduct().getItemNumber());
        }
    }
}
