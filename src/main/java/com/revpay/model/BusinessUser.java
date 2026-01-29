package com.revpay.model;

import java.util.List;

public class BusinessUser extends User {

    private String businessName;
    private String businessType;
    private String taxId;
    private String address;
    private List<String> verificationDocuments; // file paths or doc IDs

    // Getters & Setters
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<String> getVerificationDocuments() { return verificationDocuments; }
    public void setVerificationDocuments(List<String> verificationDocuments) { this.verificationDocuments = verificationDocuments; }

}
