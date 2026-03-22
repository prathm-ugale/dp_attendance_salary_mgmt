package com.labour.attendance.dto;

public class AttendanceSummaryDTO {

    private Long labourId;
    private String codeNo;
    private String name;
    private Double shiftsWorked;
    private Double kharcha1;
    private Double kharcha2;
    private Double kharcha3;
    private Double bhada;

    public AttendanceSummaryDTO(
            Long labourId,
            String codeNo,
            String name,
            Double shiftsWorked,
            Double kharcha1,
            Double kharcha2,
            Double kharcha3,
            Double bhada
    ) {
        this.labourId = labourId;
        this.codeNo = codeNo;
        this.name = name;
        this.shiftsWorked = shiftsWorked;
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

    public Double getShiftsWorked() {
        return shiftsWorked;
    }

    public void setShiftsWorked(Double shiftsWorked) {
        this.shiftsWorked = shiftsWorked;
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
