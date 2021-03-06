package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.DeliveryModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusDirectEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;
import hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions.*;
import hu.gov.allamkincstar.training.javasebsc.orderapi.stock.Stock;

import java.time.LocalDateTime;
import java.util.List;

import static hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusDirectEnum.DELIVERED;
import static hu.gov.allamkincstar.training.javasebsc.orderapi.enums.OrderStatusDirectEnum.PENDING;

public final class OrderDirect extends Order {

    protected OrderStatusDirectEnum orderStatus = PENDING;

    public static OrderDirect createOrder(Cart caller, Long orderID, List<ProductItem> orderItemList){
        if (orderItemList.size() != caller.productItemList().size()) throw new IllegalOrderCreationException();
        for (int i = 0; i < orderItemList.size(); i++){
            if (!(orderItemList.get(i).getIndex() == caller.productItemList().get(i).getIndex() ||
                    orderItemList.get(i).getQuantity() == caller.productItemList().get(i).getQuantity())
            ) throw new IllegalOrderCreationException();
        }
        return new OrderDirect(orderID, orderItemList);
    }

    private  OrderDirect(Long orderId, List<ProductItem> ordeItems) {
        super(orderId, ordeItems);
    }

    @Override
    public void dispatchOrder(Customer customer,
                              PaymentModeEnum paymentMode) throws InvalidOrderOperationException, InvalidPaymentModeException {
        this.customer = customer;
        validateCustomer();
        validatePaymentModeToSet(paymentMode);
        switch (orderStatus){
            case PENDING:
                break;
            case BOOKED:
                throw new InvalidOrderOperationException("A rendelés már feladásra került.");
            case DELIVERED:
                throw new InvalidOrderOperationException("Átvételre került rendelés nem adható fel újra");
            case CLOSED:
                throw new InvalidOrderOperationException("Lezárt rendelés nem adható fel újra");
            default:
                throw new InvalidOrderOperationException("Feladásban nem kezelt rendelésállapot: "+orderStatus);
        }
        this.paymentMode = paymentMode;
        orderStatus = OrderStatusDirectEnum.BOOKED;
        creationDate = LocalDateTime.now();
    }

    @Override
    public void dispatchOrder(Customer customer, PaymentModeEnum paymentMode, DeliveryModeEnum deliveryMode){
        throw new RuntimeException("This method cannot be used for this inherited class");
    }

    @Override
    public void validatePaymentModeToSet(PaymentModeEnum paymentModeToSet) throws InvalidPaymentModeException{
        // személyes vásárlás esetén az érvényes fizetési módok: készpénz, bankkártya
        if (!(paymentModeToSet == PaymentModeEnum.CASH || paymentModeToSet == PaymentModeEnum.CREDIT_CARD))
            throw new InvalidPaymentModeException(paymentModeToSet);
    }

    @Override
    public void closeOrder(Stock stock) throws NotEnoughItemException, InvalidQuantityArgumentException, InvalidOrderOperationException {
        if (orderStatus != OrderStatusDirectEnum.DELIVERED) {
            throw new InvalidOrderOperationException("Nem véglegesített rendelés nem zárható le.");
        }
        finishAllProduct(stock);
        closedDate = LocalDateTime.now();
        orderStatus = OrderStatusDirectEnum.CLOSED;
    }

    public void confirmPayment() throws InvalidOrderOperationException {
        switch (orderStatus){
            case PENDING:
                throw new InvalidOrderOperationException("A rendelés még nem került feladásra.");
            case BOOKED:
                orderStatus = DELIVERED;
                break;
            case DELIVERED:
                throw new InvalidOrderOperationException("A rendelés már korábban átvételre került");
            case CLOSED:
                throw new InvalidOrderOperationException("Lezárt rendelés fizetése nem nyugtázható");
            default:
                throw new InvalidOrderOperationException("Fizetésmegerősítésben nem kezelt rendelésállapot: "+orderStatus);
        }
        paid          = true;
        paidDate      = LocalDateTime.now();
        deliveredDate = LocalDateTime.now();
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public PaymentModeEnum getPaymentMode() {
        return paymentMode;
    }

    public OrderStatusDirectEnum getOrderStatus() {
        return orderStatus;
    }

}
