package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private long id;
    private long businessUserId;
    private long customerUserId;
    private BigDecimal totalAmount;
    private String status; // UNPAID, PAID, OVERDUE
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBusinessUserId() { return businessUserId; }
    public void setBusinessUserId(long businessUserId) { this.businessUserId = businessUserId; }
    public long getCustomerUserId() { return customerUserId; }
    public void setCustomerUserId(long customerUserId) { this.customerUserId = customerUserId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
