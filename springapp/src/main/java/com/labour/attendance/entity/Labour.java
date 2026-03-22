package com.labour.attendance.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "labours")
public class Labour {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_no", unique = true, nullable = false)
    private String codeNo;

    @Column(nullable = false)
    private String name;
    private String occupation;

        // inside Labour class
    @Column(name = "per_day_money")
    private Double perDayMoney;

    @Column(name = "monthly_k1")
    private Double monthlyK1;
    @Column(name = "monthly_k2")
    private Double monthlyK2;
    @Column(name = "monthly_k3")
    private Double monthlyK3;
    @Column(name = "monthly_bhada")
    private Double monthlyBhada;

    private String aadhar;
    private String mobile;
    private String site;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeNo() { return codeNo; }
    public void setCodeNo(String codeNo) { this.codeNo = codeNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    // getters/setters
    public Double getPerDayMoney() {
        return perDayMoney;
    }

    public void setPerDayMoney(Double perDayMoney) {
        this.perDayMoney = perDayMoney;
    }

    public Double getMonthlyK1() {
        return monthlyK1;
    }

    public void setMonthlyK1(Double monthlyK1) {
        this.monthlyK1 = monthlyK1;
    }

    public Double getMonthlyK2() {
        return monthlyK2;
    }

    public void setMonthlyK2(Double monthlyK2) {
        this.monthlyK2 = monthlyK2;
    }

    public Double getMonthlyK3() {
        return monthlyK3;
    }

    public void setMonthlyK3(Double monthlyK3) {
        this.monthlyK3 = monthlyK3;
    }

    public Double getMonthlyBhada() {
        return monthlyBhada;
    }

    public void setMonthlyBhada(Double monthlyBhada) {
        this.monthlyBhada = monthlyBhada;
    }
}
