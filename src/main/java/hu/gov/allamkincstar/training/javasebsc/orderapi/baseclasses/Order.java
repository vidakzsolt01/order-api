package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidPaymentModeException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.Customer;
import hu.gov.allamkincstar.training.javasebsc.orderapi.order.OrderItem;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Order {

    protected final Long orderID;
    protected PaymentModeEnum paymentMode = null;
    protected Customer customer;
    protected Integer netSum;
    protected Integer VATSum;
    protected Integer grossSum;
    protected Integer billTotal;
    protected LocalDateTime creationDate;
    protected final List<ProductItem> orderItems;

    public Boolean getPaid() {
        return paid;
    }

    protected Boolean paid = Boolean.FALSE;

    public Order(Long orderID, List<ProductItem> ordeItems) {
        this.orderID = orderID;
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

    public abstract void dispatchOrder(Customer customer, PaymentModeEnum paymentMode) throws InvalidOrderOperationException;

    public abstract void dispatchOrder(Customer customer, PaymentModeEnum paymentMode, DeliveryModeEnum deliveryMode) throws InvalidOrderOperationException;

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

    public abstract void closeOrder(Stock stock)  throws InvalidOrderOperationException, NotEnoughItemException, InvalidQuantityArgumentException;

    public abstract Customer getCustomer();

    public abstract void setCustomer(Customer customer);

    public List<OrderItem> productItems(){
        List<OrderItem> result = new ArrayList<>();
        orderItems.forEach( orderItem -> result.add(new OrderItem(orderItem)));
        return result;
    }

    public List<ProductItem> getOrderItems(){
        return orderItems;
    }

    protected void finishAllProduct(Stock stock) throws InvalidQuantityArgumentException, NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.finishItemBook(item);
        }
    }

}
