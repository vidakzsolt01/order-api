package order;

import baseclasses.*;
import exceptions.NoItemFounException;
import exceptions.NotEnoughItemException;

import java.util.Map;

public class Order extends Container {

    private Customer customer = null;
    private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    private PaymentModeEnum paymentMode = null;
    private DeliveryModeEnum deliveryMode = null;

    public Order() {
        super();
    }

    public Order(Lot item) {
        super(item);
    }

    /*
    private final Map<String, OrderItem> orderItems;

    public Order() {
        orderItems = new HashMap<>();
    }

    public Order(OrderItem item) {
        this();
        orderItems.put(item.getProduct().getItemNumber(), item);
    }

    @Override
    public void registerNewItem(Lot item) {
        if (orderItems.containsKey(item.getProduct().getItemNumber())){
            OrderItem itemInOrder = orderItems.get(item.getProduct().getItemNumber());
            itemInOrder.setQuantity(itemInOrder.getQuantity() + item.getQuantity());
        } else {
            orderItems.put(item.getProduct().getItemNumber(), (OrderItem)item);
        }
    }

    @Override
    public void removeItem(Lot lot) throws NoItemFounException {
        if (!orderItems.containsKey(lot.getProduct().getItemNumber())) throw new NoItemFounException();
        orderItems.remove(lot.getProduct().getItemNumber());
    }

    @Override
    public void changeItemQuantity(Lot item, Integer quantity) throws NotEnoughItemException {
        item.changeItemQuantity(quantity);
    }

    @Override
    public void disposeEmptyItem(Lot item) throws NoItemFounException {
        if (item.getQuantity() == 0) removeItem(item);
    }
*/

    public Map<String, Lot> increaseItemQuantity(OrderItem item) throws NoItemFounException, NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item){
            throw new NoItemFounException(item);
        }
        changeItemQuantity(item, 1);
        return containerItems;
    }

    public Map<String, Lot> decreaseItemQuantity(OrderItem item) throws NoItemFounException, NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item) throw new NoItemFounException(item);
        changeItemQuantity(item, -1);
        return containerItems;
    }

}
