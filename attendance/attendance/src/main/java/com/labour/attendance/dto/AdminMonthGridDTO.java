package com.labour.attendance.dto;

import java.util.List;

public class AdminMonthGridDTO {

    private int year;
    private int month;
    private int daysInMonth;
    private List<AdminMonthGridRowDTO> labours;

    public AdminMonthGridDTO() {
    }

    public AdminMonthGridDTO(int year, int month, int daysInMonth, List<AdminMonthGridRowDTO> labours) {
        this.year = year;
        this.month = month;
        this.daysInMonth = daysInMonth;
        this.labours = labours;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public List<AdminMonthGridRowDTO> getLabours() {
        return labours;
    }

    public void setLabours(List<AdminMonthGridRowDTO> labours) {
        this.labours = labours;
    }
}
