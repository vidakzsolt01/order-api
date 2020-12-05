package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidPaymentModeException;

import java.util.List;

public final class OrderOnline extends Order{

    private final ImmutableList         orderItems;
    private final DeliveryParameters    deliveryParameters;
    private       OrderStatusOnlineEnum orderStatus    = OrderStatusOnlineEnum.PENDING;
    private       DeliveryModeEnum      deliveryMode   = null;
    private       String                failureComment = null;

    public OrderOnline(List<ProductItem> orderItemList, DeliveryParameters deliveryParameters){
        super(orderItemList);
        orderItems              = new ImmutableList(orderItemList);
        this.deliveryParameters = deliveryParameters;
        if (this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
    }

    @Override
    public void doOrder(Customer customer, PaymentModeEnum paymentMode) throws InvalidOrderOperationException{
        validateCustomer();
        if (paymentMode == null) throw new InvalidOrderOperationException("Nem választott fizetési módot");
        orderStatus = OrderStatusOnlineEnum.BOOKED;
    }

    @Override
    public void validatePaymentModeToSet() throws InvalidPaymentModeException{
        if (paymentMode == PaymentModeEnum.CASH)
            throw new InvalidPaymentModeException(paymentMode);
    }

    @Override
    public Customer getCustomer(){
        return customer;
    }

    @Override
    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    @Override
    public void confirmPayment(){
        if (orderStatus == OrderStatusOnlineEnum.BOOKED) {
            orderStatus = OrderStatusOnlineEnum.WAITING_FOR_DELIVERY;
            paid        = true;
        }
    }

    @Override
    public PaymentModeEnum getPaymentMode(){
        return paymentMode;
    }

    public void passToDeliveryService() throws InvalidOrderOperationException{
        if (orderStatus != OrderStatusOnlineEnum.WAITING_FOR_DELIVERY) {
            throw new InvalidOrderOperationException("A rendelés nem kész a futárnak való átadásra.");
        }
        if (paymentMode != PaymentModeEnum.ADDITIONAL) {
            if (paid) {
                orderStatus = OrderStatusOnlineEnum.IN_PROGRESS;
            } else {
                throw new InvalidOrderOperationException("Utalás vagy bankkártyás fizetés esetén a csomag nem adható át a futárnak, amíg a számla nincs kiegyenlítve.");
            }
        } else {
            orderStatus = OrderStatusOnlineEnum.IN_PROGRESS;
        }
    }

    public void deliveryConfirm(boolean deliverySuccess, String failureComment) throws InvalidOrderOperationException{
        if (orderStatus != OrderStatusOnlineEnum.IN_PROGRESS) {
            throw new InvalidOrderOperationException("Nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható");
        }
        if (deliverySuccess) {
            paid        = true;
            orderStatus = OrderStatusOnlineEnum.DELIVERED;
        } else {
            if (failureComment != null && !failureComment.isBlank()) {
                this.failureComment = failureComment;
                orderStatus         = OrderStatusOnlineEnum.FAILED_DELIVERY;
            } else {
                throw new InvalidOrderOperationException("Sikertelen átvétel esetén a sikertelenség okát fel kell tüntetni");
            }
        }
    }

    public void orderClose() throws InvalidOrderOperationException{
        if (!paid) throw new InvalidOrderOperationException("Számla nincs kiegyenlítve, rendelés nem zárható le.");
        if (!(orderStatus == OrderStatusOnlineEnum.DELIVERED || orderStatus == OrderStatusOnlineEnum.FAILED_DELIVERY)) {
            throw new InvalidOrderOperationException("Nem véglegesített rendelés nem zárható le.");
        }
        if (orderStatus == OrderStatusOnlineEnum.WAITING_FOR_DELIVERY) {
            //TODO itt lehet implementálni/meghívni a metódust, mely
            // felszabadítja raktárkészleten a rendeléssel lefoglalt mennyiségeket
        } else {
            //TODO itt lehet implementálni/meghívni a metódust, mely véglegesíti a
            // raktárkészleten a rendeléssel kiment mennyiségeket
        }
    }

    public ImmutableList getOrderItems(){
        return orderItems;
    }

    public OrderStatusOnlineEnum getOrderStatus(){
        return orderStatus;
    }

    public void setOrderStatus(OrderStatusOnlineEnum orderStatus){
        this.orderStatus = orderStatus;
    }

    @Override
    public void setPaymentMode(PaymentModeEnum paymentMode) throws InvalidPaymentModeException{
        validatePaymentModeToSet();
        this.paymentMode = paymentMode;
    }

    public DeliveryModeEnum getDeliveryMode(){
        return deliveryMode;
    }

    public void setDeliveryMode(DeliveryModeEnum deliveryMode){
        this.deliveryMode = deliveryMode;
    }

    public String getFailureComment(){
        return failureComment;
    }

    public void setFailureComment(String failureComment){
        this.failureComment = failureComment;
    }

}
