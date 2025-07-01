package com.smartfinance.smart_finance_manager.dto;

import java.time.LocalDate;

public class DebtRecoveryDTO {

    private Long id;
    private String borrowerName;
    private double amount;
    private LocalDate dateLent;
    private String description;
    private String status;

    public DebtRecoveryDTO() {}

    public DebtRecoveryDTO(Long id, String borrowerName, double amount,
                           LocalDate dateLent, String description, String status) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.amount = amount;
        this.dateLent = dateLent;
        this.description = description;
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public LocalDate getDateLent() {
        return dateLent;
    }

    public void setDateLent(LocalDate dateLent) {
        this.dateLent = dateLent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

