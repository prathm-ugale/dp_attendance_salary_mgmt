package com.labour.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
public class Rate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "labour_id")
    private Labour labour;

    @Column(nullable = false)
    private Double perDayRate;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Labour getLabour() { return labour; }
    public void setLabour(Labour labour) { this.labour = labour; }

    public Double getPerDayRate() { return perDayRate; }
    public void setPerDayRate(Double perDayRate) { this.perDayRate = perDayRate; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
}
