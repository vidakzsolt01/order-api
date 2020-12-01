package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidOrderOperationException;

import java.util.Map;

public class OrderOnline extends Order{

    private final ImmutableList orderItems;
    private PaymentModeOnlineEnum paymentMode = null;
    private final DeliveryParameters deliveryParameters;
    private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    private DeliveryModeEnum deliveryMode = null;
    private String failureComment = null;

    public OrderOnline(Map<String, Lot> orderItemList, DeliveryParameters deliveryParameters) {
        super(orderItemList);
        orderItems = new ImmutableList(orderItemList);
        this.deliveryParameters = deliveryParameters;
        if (this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
    }

    //TODO implementálni: rendelésfeladás - order()
    public void doOrder(){
        orderStatus = OrderStatusEnum.BOOKED;
    };

    //TODO implementálni: fizetés nyugtázása - paymentConfirm()
    public void confirmPayment(){
        if (orderStatus == OrderStatusEnum.BOOKED){
            orderStatus = OrderStatusEnum.WAITING_FOR_DELIVERY;
        }
    };

    //TODO implementálni: átadás a futárszolgálatnak - passToDeliveryService()
    public void passToDeliveryService() throws InvalidOrderOperationException {
        if (orderStatus != OrderStatusEnum.WAITING_FOR_DELIVERY){
            throw new InvalidOrderOperationException("A rendelés nem kész a futárnak való átadásra");
        }
        if (paymentMode != PaymentModeOnlineEnum.ADDITIONAL){
            if (paid){
                orderStatus = OrderStatusEnum.IN_PROGRESS;
            } else {
                throw new InvalidOrderOperationException("Utalás vagy bankkártyás fizetés esetén a csomag nem adható át a futárnak, amíg a számla nincs kiegyenlítve.");
            }
        } else {
            orderStatus = OrderStatusEnum.IN_PROGRESS;
        }
    }

    //TODO implementálni: szállítás nyugtázása - deliveryConfirm(boolean success, (optional) String failureComment)
    public void deliveryConfirm(boolean deliverySuccess, String failureComment) throws InvalidOrderOperationException {
        if (orderStatus != OrderStatusEnum.IN_PROGRESS){
            throw new InvalidOrderOperationException("Nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható");
        }
        if (deliverySuccess){
            orderStatus = OrderStatusEnum.DELIVERED;
        } else {
            if (failureComment != null && !failureComment.isBlank()){
                this.failureComment = failureComment;
                orderStatus = OrderStatusEnum.FAILED_DELIVERY;
            } else {
                throw new InvalidOrderOperationException("Sikertelen átvétel esetén a sikertelenség okát fel kell tüntetni");
            }
        }
    }

    //TODO implementálni: rendelés lezárása - orderClose()
    public void orderClose() throws InvalidOrderOperationException {
        if (!(orderStatus == OrderStatusEnum.DELIVERED || orderStatus == OrderStatusEnum.FAILED_DELIVERY)){
            throw new InvalidOrderOperationException("Nem véglegesített rendelés nem zárható le.");
        }
        //TODO implementálni és meghívni a metódust, mely véglegesíti a
        // raktárkészleten a rendeléssel kiment mennyiségeket

    }

    public ImmutableList getOrderItems() {
        return orderItems;
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

    public void setOrderStatus(OrderStatusEnum orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentModeOnlineEnum getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentModeOnlineEnum paymentMode) {
        this.paymentMode = paymentMode;
    }

    public DeliveryModeEnum getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(DeliveryModeEnum deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getFailureComment() {
        return failureComment;
    }

    public void setFailureComment(String failureComment) {
        this.failureComment = failureComment;
    }
}
