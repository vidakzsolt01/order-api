package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.DeliveryModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusOnlineEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

import java.time.LocalDateTime;
import java.util.List;

public final class OrderOnline extends Order{

    private final DeliveryParameters    deliveryParameters;
    private OrderStatusOnlineEnum orderStatus    = OrderStatusOnlineEnum.PENDING;
    private DeliveryModeEnum      deliveryMode   = null;
    private String                failureComment = null;
    private final Integer         billTotal;
    private LocalDateTime         passedToServiceDate = null;

    public static OrderOnline createOrder(Cart caller, Long orderID, List<ProductItem> orderItemList, DeliveryParameters deliveryParameters){
        if (orderItemList.size() != caller.productItemList().size()) throw new IllegalOrderCreationException();
        for (int i = 0; i < orderItemList.size(); i++){
            if (!(orderItemList.get(i).getIndex() == caller.productItemList().get(i).getIndex() ||
                  orderItemList.get(i).getQuantity() == caller.productItemList().get(i).getQuantity())
               ) throw new IllegalOrderCreationException();
        }
        return new OrderOnline(orderID, orderItemList, deliveryParameters);
    }

    private OrderOnline(Long orderID, List<ProductItem> orderItemList, DeliveryParameters deliveryParameters){
        super(orderID, orderItemList);
        this.deliveryParameters = deliveryParameters;
        if (this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
        else billTotal = grossSum;
    }

    @Override
    public void dispatchOrder(Customer customer, PaymentModeEnum paymentMode){
        throw new RuntimeException("This method cannot be used in this class");
    }

    @Override
    public void dispatchOrder(Customer customer, PaymentModeEnum paymentMode, DeliveryModeEnum deliveryMode) throws InvalidOrderOperationException, InvalidPaymentModeException {
        validatePaymentModeToSet(paymentMode);
        this.customer = customer;
        this.paymentMode = paymentMode;
        this.deliveryMode = deliveryMode;
        validateCustomer();
        if (deliveryMode == null) throw new InvalidOrderOperationException("Nem választott szállítási módot");
        if (orderStatus != OrderStatusOnlineEnum.PENDING) throw new InvalidOrderOperationException("Feladás csak előkészítés státuszban kezdeményezhető");
        // ha a fizetési mód "utánvét", akkor egyből WAITING_FOR_DELIVERY lesz, egyébként BOOKED
        orderStatus = (paymentMode == PaymentModeEnum.ADDITIONAL) ? OrderStatusOnlineEnum.WAITING_FOR_DELIVERY : OrderStatusOnlineEnum.BOOKED;
        creationDate = LocalDateTime.now();
    }

    @Override
    public void validatePaymentModeToSet(PaymentModeEnum paymentModeToSet) throws InvalidPaymentModeException{
        // online megrendelés esetén érvényes fizetési módok: utávét, utalás, bankkártya
        if (!(paymentModeToSet == PaymentModeEnum.ADDITIONAL ||
              paymentModeToSet == PaymentModeEnum.BY_WIRE    ||
              paymentModeToSet == PaymentModeEnum.CREDIT_CARD))
            throw new InvalidPaymentModeException(paymentModeToSet);
    }

    @Override
    public Customer getCustomer(){
        return customer;
    }

    @Override
    public void confirmPayment(){
        if (orderStatus == OrderStatusOnlineEnum.BOOKED &&
            !paid &&
            paidDate == null) {
            orderStatus = (deliveryMode == DeliveryModeEnum.DELIVERY_SERVICE) ?
                    OrderStatusOnlineEnum.WAITING_FOR_DELIVERY :
                    OrderStatusOnlineEnum.DELIVERED;
            paidDate    = LocalDateTime.now();
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
            throw  new InvalidOrderOperationException("Sikertelen átvétel esetén a sikertelenség okát fel kell tűntetni");

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
        if (paidDate == null) paidDate = LocalDateTime.now();
    }

    @Override
    public void closeOrder(Stock stock) throws InvalidOrderOperationException, NotEnoughItemException, InvalidQuantityArgumentException {
        if (!paid) throw new InvalidOrderOperationException("Számla nincs kiegyenlítve, a rendelés nem zárható le.");
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

/*
    private void releaseAllProduct(Stock stock) throws NotEnoughItemException {
        for (OrderItem item :productItems()){
            stock.releaseBookedQuantity(item);
        }
    }

*/
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

    public DeliveryModeEnum getDeliveryMode(){
        return deliveryMode;
    }

    public String getFailureComment(){
        return failureComment;
    }

    public DeliveryParameters getDeliveryParameters() {
        return deliveryParameters;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
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

    public Integer getBillTotal(){
        return billTotal;
    }
}
