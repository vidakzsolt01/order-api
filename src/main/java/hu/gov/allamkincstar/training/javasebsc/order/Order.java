package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.exceptions.InvalidOrderOperationException;

import java.util.Map;

public class Order {

    //private final List<OrderItem> orderItems;
    private final ImmutableList orderItems;
    private Customer customer = null;
    private final DeliveryParameters deliveryParameters;
    private Integer billTotal;
    private OrderStatusEnum orderStatus = OrderStatusEnum.PENDING;
    private PaymentModeEnum paymentMode = null;
    private DeliveryModeEnum deliveryMode = null;
    private String failureComment = null;
    private final Integer netSum;
    private final Integer VATSum;
    private final Integer grossSum;
    private final ShoppingModeEnum shoppingMode;
    private Boolean paid = Boolean.FALSE;

    public Order(Map<String, Lot> orderItemList, DeliveryParameters deliveryParameters, ShoppingModeEnum shoppingMode) {
        int net = 0;
        int VAT = 0;
        int gross = 0;
        orderItems = new ImmutableList(orderItemList);
        netSum = net;
        VATSum = VAT;
        grossSum = gross;
        this.deliveryParameters = deliveryParameters;
        this.shoppingMode = shoppingMode;
        this.billTotal = grossSum;
        if (shoppingMode == ShoppingModeEnum.ONLINE && this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
    }

    //TODO implementálni: rendelésfeladás - order()
    public void doOrder() throws InvalidOrderOperationException {
        if (shoppingMode == ShoppingModeEnum.ONLINE){
            orderStatus = OrderStatusEnum.BOOKED;
        } else {
            throw new InvalidOrderOperationException("Érvénytelen művelet: közvetlen bolti vásárlás esetén nem lehet feladni a rendelést");
        }
    };

    //TODO implementálni: fizetés nyugtázása - paymentConfirm()
    public void confirmPayment(){
        if (orderStatus == OrderStatusEnum.BOOKED){
            if (shoppingMode == ShoppingModeEnum.ONLINE){
                orderStatus = OrderStatusEnum.WAITING_FOR_DELIVERY;
            } else {
                orderStatus = OrderStatusEnum.DELIVERED;
            }
        }
    };

    //TODO implementálni: átadás a futárszolgálatnak - passToDeliveryService()
    public void passToDeliveryService() throws InvalidOrderOperationException {
        if (orderStatus != OrderStatusEnum.WAITING_FOR_DELIVERY){
            throw new InvalidOrderOperationException("Érvénytelen művelet: a rendelés nem kész a futárnak való átadásra");
        }
        if (shoppingMode == ShoppingModeEnum.ONLINE){
            if (paymentMode != PaymentModeEnum.CASH){
                if (paid){
                    orderStatus = OrderStatusEnum.IN_PROGRESS;
                } else {
                    throw new InvalidOrderOperationException("Érvénytelen művelet: nem készpénzes vásrlás esetén amíg a számla nincs kiegyenlítve, nem adható át a futárnak");
                }
            } else {
                orderStatus = OrderStatusEnum.IN_PROGRESS;
            }
        } else {
            throw new InvalidOrderOperationException("Érvénytelen művelet: közvetlen vásárlás esetén a rendelés nem adható át a futárnak");
        }
    }

    //TODO implementálni: szállítás nyugtázása - deliveryConfirm(boolean success, (optional) String failureComment)
    public void deliveryConfirm(boolean deliverySuccess, String failureComment) throws InvalidOrderOperationException {
        if (orderStatus != OrderStatusEnum.IN_PROGRESS){
            throw new InvalidOrderOperationException("Érvénytelen művelet: nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható");
        }
        if (deliverySuccess){
            orderStatus = OrderStatusEnum.DELIVERED;
        } else {
            if (failureComment != null && !failureComment.isBlank()){
                this.failureComment = failureComment;
                orderStatus = OrderStatusEnum.FAILED_DELIVERY;
            } else {
                throw new InvalidOrderOperationException("Érvénytelen művelet: sikertelen átvétel esetén a sikertelenség oka nélkül a szállítás nem nyugtázható");
            }
        }
    }

    //TODO implementálni: rendelés lezárása - orderClose()
    public void orderClose() throws InvalidOrderOperationException {
        if (!(orderStatus == OrderStatusEnum.DELIVERED || orderStatus == OrderStatusEnum.FAILED_DELIVERY)){
            throw new InvalidOrderOperationException("Érvénytelen művelet: nem véglegesített rendelés nem zárható le.");
        }
        //TODO implementálni és meghívni a metódust, mely véglegesíti a
        // raktárkészleten a rendelésben lefoglalt mennyiségeket

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

    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentModeEnum paymentMode) {
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
