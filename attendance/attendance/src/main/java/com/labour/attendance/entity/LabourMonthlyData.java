package com.labour.attendance.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "labour_monthly_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"labour_id", "year_no", "month_no"})
)
public class LabourMonthlyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "labour_id")
    private Labour labour;

    @Column(name = "year_no", nullable = false)
    private Integer year;

    @Column(name = "month_no", nullable = false)
    private Integer month;

    @Column(name = "per_day_money")
    private Double perDayMoney;

    @Column(name = "kharcha1")
    private Double kharcha1;

    @Column(name = "kharcha2")
    private Double kharcha2;

    @Column(name = "kharcha3")
    private Double kharcha3;

    @Column(name = "bhada")
    private Double bhada;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Labour getLabour() {
        return labour;
    }

    public void setLabour(Labour labour) {
        this.labour = labour;
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

    public Double getPerDayMoney() {
        return perDayMoney;
    }

    public void setPerDayMoney(Double perDayMoney) {
        this.perDayMoney = perDayMoney;
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
