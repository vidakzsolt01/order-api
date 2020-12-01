package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.exceptions.NotEnoughItemException;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDirect extends Order {

    private PaymentModeOnlineEnum paymentMode = null;

    public OrderDirect(Map<String, Lot> ordeItems) {
        super(ordeItems);
    }

    @Override
    public void doOrder() throws InvalidOrderOperationException {
        switch (orderStatus){
            case PENDING:
                orderStatus = OrderStatusEnum.BOOKED;
                break;
            case BOOKED:
                throw new InvalidOrderOperationException("A rendelés már feladásra került.");
            case DELIVERED:
                throw new InvalidOrderOperationException("A rendelés már korábban lezárva");
            default:
                throw new InvalidOrderOperationException("Feladásban nem kezelt rendelésállapot: "+orderStatus);
        }
    }

    //TODO implementálni: fizetés nyugtázása - paymentConfirm()
    public void confirmPayment() throws InvalidOrderOperationException {
        switch (orderStatus){
            case PENDING:
                throw new InvalidOrderOperationException("A rendelés még nem került feladásra.");
            case BOOKED:
                orderStatus = OrderStatusEnum.DELIVERED;
                break;
            case DELIVERED:
                throw new InvalidOrderOperationException("A rendelés már korábban lezárva");
            default:
                throw new InvalidOrderOperationException("Lezárásban nem kezelt rendelésállapot: "+orderStatus);
        }
    }

    //TODO implementálni: rendelés lezárása - orderClose()
    public void orderClose() throws InvalidOrderOperationException {
        if (orderStatus != OrderStatusEnum.DELIVERED){
            throw new InvalidOrderOperationException("Rendelés még nincs lezárva.");
        }
        //TODO implementálni és meghívni a metódust, mely véglegesíti a
        // raktárkészleten a rendelésben lefoglalt mennyiségeket

    }
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatusEnum getOrderStatus() {
        return orderStatus;
    }

    public PaymentModeOnlineEnum getPaymentMode() {
        return paymentMode;
    }

}
