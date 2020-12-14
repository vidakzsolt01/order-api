package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.DeliveryModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;
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

public abstract class Order {

    protected final Long            orderID;
    protected       PaymentModeEnum paymentMode = null;
    protected       Customer        customer;
    protected final Integer         netSum;
    protected final Integer         VATSum;
    protected final Integer         grossSum;
    protected       LocalDateTime   creationDate = null;
    protected       LocalDateTime   payedDate = null;
    protected       LocalDateTime   closedDate = null;
    protected       LocalDateTime   deliveredDate = null;

    protected final List<ProductItem> orderItems;

    public Boolean getPaid() {
        return paid;
    }

    protected Boolean paid = Boolean.FALSE;

    public Order(Long orderID, List<ProductItem> ordeItems) {
        this.orderID = orderID;
        this.orderItems = ordeItems;
        //this.orderItems = ordeItems;
        int netSum = 0;
        int VATSum = 0;
        for (ProductItem item : ordeItems) {
            OrderItem orderItem = new OrderItem(item);
            netSum += orderItem.getNetAmount();
            VATSum += orderItem.getVATAmount();
        }
        this.netSum = netSum;
        this.VATSum = VATSum;
        grossSum = netSum + VATSum;
    }

    public abstract void dispatchOrder(Customer customer, PaymentModeEnum paymentMode) throws InvalidOrderOperationException, InvalidPaymentModeException;

    public abstract void dispatchOrder(Customer customer, PaymentModeEnum paymentMode, DeliveryModeEnum deliveryMode) throws InvalidOrderOperationException, InvalidPaymentModeException;

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

    public abstract void validatePaymentModeToSet(PaymentModeEnum paymentMode) throws InvalidPaymentModeException;

    public abstract void closeOrder(Stock stock)  throws InvalidOrderOperationException, NotEnoughItemException, InvalidQuantityArgumentException;

    public abstract Customer getCustomer();

    public List<OrderItem> productItems(){
        List<OrderItem> result = new ArrayList<>();
        orderItems.forEach( orderItem -> result.add(new OrderItem(orderItem)));
        return result;
    }

    public List<ProductItem> getOrderItems(){
        return orderItems;
    }

    protected void releaseAllProduct(Stock stock) throws NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.releaseBookedQuantity(item);
        }
    }

    protected void finishAllProduct(Stock stock) throws InvalidQuantityArgumentException, NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.finishItemBook(item);
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getPayedDate() {
        return payedDate;
    }

    public LocalDateTime getClosedDate() {
        return closedDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }
}
