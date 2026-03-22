package com.labour.attendance.dto;

import java.time.LocalDate;

public class AdminAttendanceRowDTO {

    private Long attendanceId;
    private LocalDate date;
    private Long labourId;
    private String codeNo;
    private String name;
    private String aadhar;
    private String mobile;
    private Double perDayMoney;
    private Double shiftsWorked;
    private Boolean lossOfPay;
    private Double kharcha1;
    private Double kharcha2;
    private Double kharcha3;
    private Double bhada;

    public AdminAttendanceRowDTO(
            Long attendanceId,
            LocalDate date,
            Long labourId,
            String codeNo,
            String name,
            String aadhar,
            String mobile,
            Double perDayMoney,
            Double shiftsWorked,
            Boolean lossOfPay,
            Double kharcha1,
            Double kharcha2,
            Double kharcha3,
            Double bhada
    ) {
        this.attendanceId = attendanceId;
        this.date = date;
        this.labourId = labourId;
        this.codeNo = codeNo;
        this.name = name;
        this.aadhar = aadhar;
        this.mobile = mobile;
        this.perDayMoney = perDayMoney;
        this.shiftsWorked = shiftsWorked;
        this.lossOfPay = lossOfPay;
        this.kharcha1 = kharcha1;
        this.kharcha2 = kharcha2;
        this.kharcha3 = kharcha3;
        this.bhada = bhada;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Double getPerDayMoney() {
        return perDayMoney;
    }

    public void setPerDayMoney(Double perDayMoney) {
        this.perDayMoney = perDayMoney;
    }

    public Double getShiftsWorked() {
        return shiftsWorked;
    }

    public void setShiftsWorked(Double shiftsWorked) {
        this.shiftsWorked = shiftsWorked;
    }

    public Boolean getLossOfPay() {
        return lossOfPay;
    }

    public void setLossOfPay(Boolean lossOfPay) {
        this.lossOfPay = lossOfPay;
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
