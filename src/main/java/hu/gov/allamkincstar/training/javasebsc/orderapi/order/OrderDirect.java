package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidPaymentModeException;

import java.util.List;

import static hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.OrderStatusDirectEnum.DELIVERED;
import static hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.OrderStatusDirectEnum.PENDING;

public final class OrderDirect extends Order {

    protected OrderStatusDirectEnum orderStatus = PENDING;
    private Customer customer;
    private PaymentModeEnum paymentMode;

    public OrderDirect(List<ProductItem> ordeItems) {
        super(ordeItems);
    }


    @Override
    public void doOrder(Customer customer,
                        PaymentModeEnum paymentMode) throws InvalidOrderOperationException {
        this.customer    = customer;
        this.paymentMode = paymentMode;
        switch (orderStatus){
            case PENDING:
                orderStatus = OrderStatusDirectEnum.BOOKED;
                break;
            case BOOKED:
                throw new InvalidOrderOperationException("A rendelés már feladásra került.");
            case DELIVERED:
                throw new InvalidOrderOperationException("A rendelés már korábban lezárva");
            default:
                throw new InvalidOrderOperationException("Feladásban nem kezelt rendelésállapot: "+orderStatus);
        }
    }

    @Override
    public void validatePaymentModeToSet() throws InvalidPaymentModeException{
        if (paymentMode == PaymentModeEnum.BY_WIRE || paymentMode == PaymentModeEnum.ADDITIONAL)
            throw new InvalidPaymentModeException(paymentMode);
    }

    //TODO implementálni: fizetés nyugtázása - paymentConfirm()
    public void confirmPayment() throws InvalidOrderOperationException {
        switch (orderStatus){
            case PENDING:
                throw new InvalidOrderOperationException("A rendelés még nem került feladásra.");
            case BOOKED:
                orderStatus = DELIVERED;
                break;
            case DELIVERED:
                throw new InvalidOrderOperationException("A rendelés már korábban lezárva");
            default:
                throw new InvalidOrderOperationException("Lezárásban nem kezelt rendelésállapot: "+orderStatus);
        }
    }

    //TODO implementálni: rendelés lezárása - orderClose()
    public void orderClose() throws InvalidOrderOperationException {
        if (orderStatus != DELIVERED){
            throw new InvalidOrderOperationException("Rendelés még nincs lezárva.");
        }
        //TODO implementálni és meghívni a metódust, mely véglegesíti a
        // raktárkészleten a rendelésben lefoglalt mennyiségeket
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    @Override
    public void setPaymentMode(PaymentModeEnum paymentMode) throws InvalidPaymentModeException{
        this.paymentMode = paymentMode;
    }

    public OrderStatusDirectEnum getOrderStatus() {
        return orderStatus;
    }

}
