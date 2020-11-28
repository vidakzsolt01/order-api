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
    private String failureComment = null;

    public Order() {
        super();
    }

    public Order(Lot item) {
        super(item);
    }

    public Map<String, Lot> increaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item){
            throw new NoItemFounException(item);
        }
        changeItemQuantity(item, 1);
        return containerItems;
    }

    public Map<String, Lot> decreaseItemQuantity(OrderItem item) throws NotEnoughItemException {
        if (containerItems.get(item.getIndex()) != item) throw new NoItemFounException(item);
        changeItemQuantity(item, -1);
        return containerItems;
    }

    public void

}
