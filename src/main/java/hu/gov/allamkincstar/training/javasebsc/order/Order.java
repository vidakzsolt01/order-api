package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {

    //private final List<OrderItem> orderItems;
    private final ImmutableList orderItems;
    private Customer customer = null;
    private DeliveryParameters deliveryParameters;
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
/*
        this.orderItems = new ArrayList<>();
        for (Map.Entry<String, Lot> element : orderItemList.entrySet()) {
            OrderItem item = (OrderItem) element.getValue();
            orderItems.add(item);
            net += item.getNetAmount();
            VAT += item.getVATAmount();
            gross += item.getGrossAmount();
        }
*/
        netSum = net;
        VATSum = VAT;
        grossSum = gross;
        this.deliveryParameters = deliveryParameters;
        this.shoppingMode = shoppingMode;
        this.billTotal = grossSum;
        if (shoppingMode == ShoppingModeEnum.ONLINE && this.grossSum < deliveryParameters.getLimitForFree())
            billTotal = grossSum + deliveryParameters.getDeliveryCharge();
    }

    public List<OrderItem> getOrderItems() {
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
