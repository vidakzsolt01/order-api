package hu.gov.allamkincstar.training.javasebsc.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.order.Customer;
import hu.gov.allamkincstar.training.javasebsc.order.DeliveryParameters;
import hu.gov.allamkincstar.training.javasebsc.order.ImmutableList;
import hu.gov.allamkincstar.training.javasebsc.order.OrderItem;

import java.util.List;
import java.util.Map;

public abstract class Order {

    protected final ImmutableList orderItems;
    protected Customer customer = null;
    protected Integer netSum;
    protected Integer VATSum;
    protected Integer grossSum;
    protected Integer billTotal;
    protected Boolean paid = Boolean.FALSE;
    protected OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;

    public Order(Map<String, Lot> ordeItems) {
        netSum = 0;
        VATSum = 0;
        this.orderItems = new ImmutableList(ordeItems);
        ordeItems.entrySet().forEach( item -> {
            OrderItem orderItem = new OrderItem(item.getValue());
            netSum += orderItem.getNetAmount();
            VATSum += orderItem.getVATAmount();
        });
        grossSum = netSum + VATSum;
    }

    public abstract void doOrder() throws InvalidOrderOperationException;

    public abstract void confirmPayment() throws InvalidOrderOperationException;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

}
