package dto;

public class Product {

    private static final int DEFAULT_VAT = 27;

    protected final String itemNumber;
    protected final String itemName;
    protected Integer netUntiPrice;
    protected Integer VATPercent;

    public Product(String itemNumber, String itemName, Integer netPrice, Integer VATPercent) {
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.netUntiPrice = netPrice;
        this.VATPercent = VATPercent;
    }

    public Product(String itemNumber, String itemName, Integer netPrice) {
        this.itemNumber = itemNumber;
        this.itemName = itemName;
        this.netUntiPrice = netPrice;
        this.VATPercent = DEFAULT_VAT;
    }

    public Product(Product other) {
        this.itemNumber = other.itemNumber;
        this.itemName = other.itemName;
        this.netUntiPrice = other.netUntiPrice;
        this.VATPercent = other.VATPercent;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getNetUntiPrice() {
        return netUntiPrice;
    }

    public void setNetUntiPrice(Integer netUntiPrice) {
        this.netUntiPrice = netUntiPrice;
    }

    public Integer getVATPercent() {
        return VATPercent;
    }
}
