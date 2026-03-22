package com.labour.attendance.dto;

public class MonthlyMoneyUpdateRequest {

    private Long labourId;
    private Integer year;
    private Integer month;
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
