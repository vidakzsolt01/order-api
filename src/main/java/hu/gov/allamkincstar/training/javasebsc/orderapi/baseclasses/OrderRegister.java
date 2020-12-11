package hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses;

public class OrderRegister {
    private static Long nextOrderId = 0L;

    public static Long getNextOrderId() {
        return ++nextOrderId;
    }
}
