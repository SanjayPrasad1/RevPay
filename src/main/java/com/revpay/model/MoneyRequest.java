package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoneyRequest {

    private long id;
    private long requesterId;
    private long requesteeId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String requesterName;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getRequesterId() { return requesterId; }
    public void setRequesterId(long requesterId) { this.requesterId = requesterId; }

    public long getRequesteeId() { return requesteeId; }
    public void setRequesteeId(long requesteeId) { this.requesteeId = requesteeId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRequesterName(){
        return requesterName;
    }
    public void setRequesterName(String requesterName){
        this.requesterName = requesterName;
    }
}
