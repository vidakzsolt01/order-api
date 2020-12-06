package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidPaymentModeException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.Customer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public abstract class Order {

    protected final ArrayList<OrderItem> orderItems;
    protected     PaymentModeEnum                   paymentMode = null;
    protected Customer customer;
    protected Integer netSum;
    protected Integer VATSum;
    protected Integer grossSum;
    protected Integer billTotal;
    protected Boolean paid = Boolean.FALSE;

    public Order(ArrayList<OrderItem> ordeItems) {
        this.customer = customer;
        this.orderItems = ordeItems;
        //this.orderItems = ordeItems;
        netSum = 0;
        VATSum = 0;
        ordeItems.forEach( item -> {
            OrderItem orderItem = new OrderItem(item);
            netSum += orderItem.getNetAmount();
            VATSum += orderItem.getVATAmount();
        });
        grossSum = netSum + VATSum;
    }

    public abstract void doOrder(Customer customer, PaymentModeEnum paymentMode) throws InvalidOrderOperationException;

    protected void validateCustomer() throws InvalidOrderOperationException {
        if (customer == null){
            throw new InvalidOrderOperationException("Vásárló-adatok nélkül a rendelés nem adható fel.");
        }
        if (isInvalid(customer.getName()) ||
                isInvalid(customer.getName()) ||
                isInvalid(customer.getDeliveryAddress()) ||
                isInvalid(customer.getPhoneNumber())){
            throw new InvalidOrderOperationException("Kötelező vásárló-adatok hiányoznak, a rendelés így nem adható fel.");
        }
    }

    private boolean isInvalid(String any){
        return (any == null || any.isBlank());
    }

    public abstract void confirmPayment() throws InvalidOrderOperationException;

    public abstract PaymentModeEnum getPaymentMode();

    public abstract void setPaymentMode(PaymentModeEnum paymentMode) throws InvalidPaymentModeException;

    public abstract void validatePaymentModeToSet() throws InvalidPaymentModeException;

    public abstract Customer getCustomer();

    public abstract void setCustomer(Customer customer);

    public List<OrderItem> productItems(){
        List<OrderItem> result = new ArrayList<>();
        orderItems.forEach( orderItem -> result.add(new OrderItem(orderItem)));
        return result;
    }

    public ArrayList<OrderItem> getOrderItems(){
        return orderItems;
    }

}
