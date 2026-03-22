package com.labour.attendance.dto;

public class AdminMonthCellUpdateRequest {

    private Long labourId;
    private int year;
    private int month;
    private int day;
    private Double kharcha1;
    private Double kharcha2;
    private Double kharcha3;
    private Double bhada;

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

    public Double getKharcha1() {
        return kharcha1;
    }

    public void setKharcha1(Double kharcha1) {
        this.kharcha1 = kharcha1;
    }

    public Double getKharcha2() {
        return kharcha2;
    }

    public void setKharcha2(Double kharcha2) {
        this.kharcha2 = kharcha2;
    }

    public Double getKharcha3() {
        return kharcha3;
    }

    public void setKharcha3(Double kharcha3) {
        this.kharcha3 = kharcha3;
    }

    public Double getBhada() {
        return bhada;
    }

    public void setBhada(Double bhada) {
        this.bhada = bhada;
    }
}
