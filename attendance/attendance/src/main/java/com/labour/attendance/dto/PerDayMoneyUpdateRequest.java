package com.labour.attendance.dto;

public class PerDayMoneyUpdateRequest {
    private Integer year;
    private Integer month;
    private Double perDayMoney;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getPerDayMoney() {
        return perDayMoney;
    }

    public void setPerDayMoney(Double perDayMoney) {
        this.perDayMoney = perDayMoney;
    }
}
