package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

public class Customer {
    private final Long customerID;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String deliveryAddress;
    private String accountAddress;

    public Customer(Long customerID, String name) {
        this.customerID = customerID;
        this.name = name;
        phoneNumber = null;
        email = null;
        accountAddress = null;
        deliveryAddress = null;
    }

    public Customer(Long customerID, String name, String address, String phoneNumber, String email, String deliveryAddress) {
        this(customerID, name);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
        this.accountAddress = deliveryAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Customer(Long customerID, String name, String address, String phoneNumber, String email, String deliveryAddress, String accountAddress) {
        this(customerID, name, address, phoneNumber,email, deliveryAddress);
        this.accountAddress = accountAddress;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getName() {
        return name;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

}
