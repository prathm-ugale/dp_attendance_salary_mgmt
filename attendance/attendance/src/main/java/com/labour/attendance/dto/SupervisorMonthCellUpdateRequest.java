package com.labour.attendance.dto;

public class SupervisorMonthCellUpdateRequest {

    private Long labourId;
    private int year;
    private int month;   // 1..12
    private int day;     // 1..31
    private Double shiftsWorked;  // 0, 0.5, ...

    public Long getLabourId() {
        return labourId;
    }

    public void setLabourId(Long labourId) {
        this.labourId = labourId;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Double getShiftsWorked() {
        return shiftsWorked;
    }

    public void setShiftsWorked(Double shiftsWorked) {
        this.shiftsWorked = shiftsWorked;
    }
}
