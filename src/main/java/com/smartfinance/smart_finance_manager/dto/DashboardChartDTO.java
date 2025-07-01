package com.smartfinance.smart_finance_manager.dto;

import java.util.List;

public class DashboardChartDTO {
    private List<String> months;
    private List<Double> income;
    private List<Double> expense;

    public DashboardChartDTO(){}
    public DashboardChartDTO(List<String> months, List<Double> income, List<Double> expense) {
        this.months = months;
        this.income = income;
        this.expense = expense;
    }

    public List<String> getMonths() { return months; }
    public List<Double> getIncome() { return income; }
    public List<Double> getExpense() { return expense; }

    public void setExpense(List<Double> expense) {
        this.expense = expense;
    }

    public void setIncome(List<Double> income) {
        this.income = income;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }
}

