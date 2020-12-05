package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;

import java.util.List;

import static hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.OrderStatusDirectEnum.DELIVERED;
import static hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.OrderStatusDirectEnum.PENDING;

public class OrderDirect extends Order {

    private final PaymentModeDirectEnum paymentMode;
    protected OrderStatusDirectEnum orderStatus = PENDING;

    public OrderDirect(List<ProductItem> ordeItems, PaymentModeDirectEnum paymentMode) {
        super(ordeItems);
        this.paymentMode = paymentMode;
    }


    @Override
    public void doOrder(Customer customer, PaymentModeDirectEnum paymentMode) throws InvalidOrderOperationException {
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
    public void doOrder(Customer customer, PaymentModeOnlineEnum paymentMode) throws InvalidOrderOperationException {
        throw new RuntimeException("This methode doesn't belong to this class");
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
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PaymentModeDirectEnum getPaymentMode() {
        return paymentMode;
    }

    public OrderStatusDirectEnum getOrderStatus() {
        return orderStatus;
    }

}
