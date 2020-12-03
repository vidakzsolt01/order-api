package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.Customer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.ImmutableList;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.Map;

public abstract class Order {

    protected final ImmutableList orderItems;
    protected Customer customer = null;
    protected Integer netSum;
    protected Integer VATSum;
    protected Integer grossSum;
    protected Integer billTotal;
    protected Boolean paid = Boolean.FALSE;

    public Order(Map<String, ProductItem> ordeItems) {
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

    public abstract void doOrder(Customer customer, PaymentModeDirectEnum paymentMode) throws InvalidOrderOperationException;

    public abstract void confirmPayment() throws InvalidOrderOperationException;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
