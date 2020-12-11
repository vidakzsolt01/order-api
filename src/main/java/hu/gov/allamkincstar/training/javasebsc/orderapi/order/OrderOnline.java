package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidOrderOperationException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidPaymentModeException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.InvalidQuantityArgumentException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.NotEnoughItemException;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

import java.time.LocalDateTime;
import java.util.ArrayList;

public final class OrderOnline extends Order{

    private final DeliveryParameters    deliveryParameters;
    private       OrderStatusOnlineEnum orderStatus    = OrderStatusOnlineEnum.PENDING;
    private       DeliveryModeEnum      deliveryMode   = null;
    private       String                failureComment = null;
    private       LocalDateTime         payedDate = null;
    private       LocalDateTime         passedToServiceDate = null;
    private       LocalDateTime         deliveredDate = null;
    private       LocalDateTime         closedDate = null;

    public OrderOnline(Long orderID, ArrayList<OrderItem> orderItemList, DeliveryParameters deliveryParameters){
        super(orderID, orderItemList);
        this.deliveryParameters = deliveryParameters;
        if (this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
    }

    @Override
    public void dispatchOrder(Customer customer, PaymentModeEnum paymentMode){
        throw new RuntimeException("This method cannot be used in this class");
    }

    @Override
    public void dispatchOrder(Customer customer, PaymentModeEnum paymentMode, DeliveryModeEnum deliveryMode) throws InvalidOrderOperationException{
        this.customer = customer;
        this.paymentMode = paymentMode;
        this.deliveryMode = deliveryMode;
        validateCustomer();
        if (paymentMode == null) throw new InvalidOrderOperationException("Nem választott fizetési módot");
        if (deliveryMode == null) throw new InvalidOrderOperationException("Nem választott szállítási módot");
        if (orderStatus != OrderStatusOnlineEnum.PENDING) throw new InvalidOrderOperationException("Feladás csak előkészítés státuszban kezdeményezhető");
        // ha a fizetési mód "utánvét", akkor egyből WAITING_FOR_DELIVERY lesz, egyébként BOOKED
        orderStatus = (paymentMode == PaymentModeEnum.ADDITIONAL) ? OrderStatusOnlineEnum.WAITING_FOR_DELIVERY : OrderStatusOnlineEnum.BOOKED;
        creationDate = LocalDateTime.now();
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
            orderStatus = (deliveryMode == DeliveryModeEnum.DELIVERY_SERVICE) ?
                    OrderStatusOnlineEnum.WAITING_FOR_DELIVERY :
                    OrderStatusOnlineEnum.DELIVERED;
            payedDate = LocalDateTime.now();
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
            if (!paid) {
                throw new InvalidOrderOperationException("Utalás vagy bankkártyás fizetés esetén a csomag nem adható át a futárnak, amíg a számla nincs kiegyenlítve.");
            }
        }
        orderStatus = OrderStatusOnlineEnum.IN_PROGRESS;
        passedToServiceDate = LocalDateTime.now();
    }

    public void confirmDelivery(boolean deliverySuccess) throws InvalidOrderOperationException{
        if (orderStatus != OrderStatusOnlineEnum.IN_PROGRESS) {
            throw new InvalidOrderOperationException("Nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható");
        }
        if (!deliverySuccess)
            throw  new InvalidOrderOperationException("Sikertelen átvétel esetén a sikertelenség okát fel kell tüntetni");

        checkPaidAfterDelivery();
        orderStatus  = OrderStatusOnlineEnum.DELIVERED;
        deliveredDate = LocalDateTime.now();
    }

    public void confirmDelivery(boolean deliverySuccess, String failureComment) throws InvalidOrderOperationException{
        if (orderStatus != OrderStatusOnlineEnum.IN_PROGRESS) {
            throw new InvalidOrderOperationException("Nem kiszállítás alatt lévő megrendelés szállítása nem nyugtázható");
        }
        if (!deliverySuccess && (failureComment == null || failureComment.isBlank()))
            throw  new InvalidOrderOperationException("Sikertelen átvétel esetén a sikertelenség okát fel kell tüntetni");

        if (deliverySuccess) {
            checkPaidAfterDelivery();
            orderStatus = OrderStatusOnlineEnum.DELIVERED;
        } else {
            this.failureComment = failureComment;
            orderStatus         = OrderStatusOnlineEnum.FAILED_DELIVERY;
        }
        deliveredDate = LocalDateTime.now();
    }

    private void checkPaidAfterDelivery(){
        if (!paid)               paid = true;
        if (payedDate == null) payedDate = LocalDateTime.now();
    }

    @Override
    public void closeOrder(Stock stock) throws InvalidOrderOperationException, NotEnoughItemException, InvalidQuantityArgumentException {
        if (!paid) throw new InvalidOrderOperationException("Számla nincs kiegyenlítve, rendelés nem zárható le.");
        if (!(orderStatus == OrderStatusOnlineEnum.DELIVERED || orderStatus == OrderStatusOnlineEnum.FAILED_DELIVERY)) {
            throw new InvalidOrderOperationException("Nem véglegesített rendelés nem zárható le.");
        }
        if (orderStatus == OrderStatusOnlineEnum.FAILED_DELIVERY) {
            releaseAllProduct(stock);
        } else {
            finishAllProduct(stock);
        }
        closedDate = LocalDateTime.now();
    }

    private void releaseAllProduct(Stock stock) throws NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.releaseBookedQuantity(item);
        }
    }

/*
    private void finishAllProduct(Stock stock) throws InvalidQuantityArgumentException, NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.finishItemBook(item);
        }
    }

*/
    public OrderStatusOnlineEnum getOrderStatus(){
        return orderStatus;
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

    public DeliveryParameters getDeliveryParameters() {
        return deliveryParameters;
    }

    public LocalDateTime getPayedDate() {
        return payedDate;
    }

    public LocalDateTime getPassedToServiceDate() {
        return passedToServiceDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }

    public LocalDateTime getClosedDate() {
        return closedDate;
    }
}
