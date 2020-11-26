public class Product {
    private final String itemNumber;
    private final String itemName;
    private Double netPrice;
    private final Float VATRate;

    public Product(String itemNumber, String itemName, Double netPrice, Float VATRate) {
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.netPrice = netPrice;
        this.VATRate = VATRate;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public Double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(Double netPrice) {
        this.netPrice = netPrice;
    }

    public Float getVATRate() {
        return VATRate;
    }
}
