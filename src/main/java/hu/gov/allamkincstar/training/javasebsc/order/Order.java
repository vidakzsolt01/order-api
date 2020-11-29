package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {

    private final List<OrderItem> orderItems;
    private Customer customer;
    private OrderStatusEnum orderStatus;
    private PaymentModeEnum paymentMode;
    private DeliveryModeEnum deliveryMode;
    private String failureComment;
    private final Integer netSum;
    private final Integer VATSum;
    private final Integer grossSum;

    public Order(Map<String, Lot> orderItemList) {
        int net = 0;
        int VAT = 0;
        int gross = 0;
        orderItems = new ArrayList<>();
        for (Map.Entry<String, Lot> element : orderItemList.entrySet()) {
            OrderItem item = (OrderItem) element.getValue();
            orderItems.add(item);
            net += item.getNetAmount();
            VAT += item.getVATAmount();
            gross += item.getGrossAmount();
        }
        netSum = net;
        VATSum = VAT;
        grossSum = gross;
        customer = null;
        orderStatus = OrderStatusEnum.PENDING;
        paymentMode = null;
        deliveryMode = null;
        failureComment = null;
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
