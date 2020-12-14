package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

public class OrderRegister {
    private static Long nextOrderId = 0L;

    public static Long getNextOrderId() {
        return ++nextOrderId;
    }
}
