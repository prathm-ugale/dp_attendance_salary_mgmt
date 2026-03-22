package com.labour.attendance.dto;

import java.util.Map;

public class AdminMonthGridRowDTO {

    private Long labourId;
    private String codeNo;
    private String name;
    private String occupation;
    private String aadhar;
    private String mobile;
    private String site;
    private Double perDayMoney;
    private Map<Integer, Double> shifts;
    private Double kharcha1;
    private Double kharcha2;
    private Double kharcha3;
    private Double bhada;

    public AdminMonthGridRowDTO() {
    }

    public AdminMonthGridRowDTO(
            Long labourId,
            String codeNo,
            String name,
            String occupation,
            String aadhar,
            String mobile,
            String site,
            Double perDayMoney,
            Map<Integer, Double> shifts,
            Double kharcha1,
            Double kharcha2,
            Double kharcha3,
            Double bhada
    ) {
        this.labourId = labourId;
        this.codeNo = codeNo;
        this.name = name;
        this.occupation = occupation;
        this.aadhar = aadhar;
        this.mobile = mobile;
        this.site = site;
        this.perDayMoney = perDayMoney;
        this.shifts = shifts;
        this.kharcha1 = kharcha1;
        this.kharcha2 = kharcha2;
        this.kharcha3 = kharcha3;
        this.bhada = bhada;
    }

    public Long getLabourId() {
        return labourId;
    }

    public void setLabourId(Long labourId) {
        this.labourId = labourId;
    }

    public String getCodeNo() {
        return codeNo;
    }

    public void setCodeNo(String codeNo) {
        this.codeNo = codeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Double getPerDayMoney() {
        return perDayMoney;
    }

    public void setPerDayMoney(Double perDayMoney) {
        this.perDayMoney = perDayMoney;
    }

    public Map<Integer, Double> getShifts() {
        return shifts;
    }

    public void setShifts(Map<Integer, Double> shifts) {
        this.shifts = shifts;
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
