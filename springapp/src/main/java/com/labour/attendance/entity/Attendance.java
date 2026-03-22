package com.labour.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"labour_id","date"}))
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "labour_id")
    private Labour labour;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double shiftsWorked;

    private Boolean lossOfPay = false;

    private Double kharcha1 = 0.0;
    private Double kharcha2 = 0.0;
    private Double kharcha3 = 0.0;
    private Double bhada = 0.0;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Labour getLabour() { return labour; }
    public void setLabour(Labour labour) { this.labour = labour; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getShiftsWorked() { return shiftsWorked; }
    public void setShiftsWorked(Double shiftsWorked) { this.shiftsWorked = shiftsWorked; }

    public Boolean getLossOfPay() { return lossOfPay; }
    public void setLossOfPay(Boolean lossOfPay) { this.lossOfPay = lossOfPay; }

    public Double getKharcha1() { return kharcha1; }
    public void setKharcha1(Double kharcha1) { this.kharcha1 = kharcha1; }

    public Double getKharcha2() { return kharcha2; }
    public void setKharcha2(Double kharcha2) { this.kharcha2 = kharcha2; }

    public Double getKharcha3() { return kharcha3; }
    public void setKharcha3(Double kharcha3) { this.kharcha3 = kharcha3; }

    public Double getBhada() { return bhada; }
    public void setBhada(Double bhada) { this.bhada = bhada; }
}
