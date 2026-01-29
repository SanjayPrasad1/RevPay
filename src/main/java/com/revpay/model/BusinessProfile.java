package com.revpay.model;

public class BusinessProfile {

    private long userId;
    private String businessType;
    private String taxId;
    private String address;

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
