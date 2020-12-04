package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.Customer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.ImmutableList;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.List;

public abstract class Order {

    protected final ImmutableList orderItems;
    protected Customer customer = null;
    protected Integer netSum;
    protected Integer VATSum;
    protected Integer grossSum;
    protected Integer billTotal;
    protected Boolean paid = Boolean.FALSE;

    public Order(List<ProductItem> ordeItems) {
        netSum = 0;
        VATSum = 0;
        this.orderItems = new ImmutableList(ordeItems);
        ordeItems.forEach( item -> {
            OrderItem orderItem = new OrderItem(item);
            netSum += orderItem.getNetAmount();
            VATSum += orderItem.getVATAmount();
        });
        grossSum = netSum + VATSum;
    }

    public abstract void doOrder(Customer customer, PaymentModeDirectEnum paymentMode) throws InvalidOrderOperationException;

    public abstract void doOrder(Customer customer, PaymentModeOnlineEnum paymentMode) throws InvalidOrderOperationException;

    public abstract void confirmPayment() throws InvalidOrderOperationException;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
