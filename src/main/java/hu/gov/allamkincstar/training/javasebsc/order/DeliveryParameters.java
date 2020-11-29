package hu.gov.allamkincstar.training.javasebsc.order;

public class DeliveryParameters {

    private Integer limitForFree = 20000;
    private Integer deliveryCharge = 2500;

    public DeliveryParameters() {
    }

    public Integer getLimitForFree() {
        return limitForFree;
    }

    public void setLimitForFree(Integer limitForFree) {
        this.limitForFree = limitForFree;
    }

    public Integer getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(Integer deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
