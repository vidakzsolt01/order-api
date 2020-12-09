package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

public class DeliveryParameters{

    private final static Integer LIMITFORFREE   = 20000;
    private final static Integer DELIVERYCHARGE = 2500;

    private Integer limitForFree   = 20000;
    private Integer deliveryCharge = 2500;

    public DeliveryParameters(Integer limitForFree, Integer deliveryCharge){
        this.limitForFree   = limitForFree;
        this.deliveryCharge = deliveryCharge;
    }

    public DeliveryParameters(){
        limitForFree   = LIMITFORFREE;
        deliveryCharge = DELIVERYCHARGE;
    }

    public Integer getLimitForFree(){
        return limitForFree;
    }

    public void setLimitForFree(Integer limitForFree){
        this.limitForFree = limitForFree;
    }

    public Integer getDeliveryCharge(){
        return deliveryCharge;
    }

    public void setDeliveryCharge(Integer deliveryCharge){
        this.deliveryCharge = deliveryCharge;
    }
}
