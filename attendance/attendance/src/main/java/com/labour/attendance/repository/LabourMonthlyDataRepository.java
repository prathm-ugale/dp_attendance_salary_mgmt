package com.labour.attendance.repository;

import com.labour.attendance.entity.Labour;
import com.labour.attendance.entity.LabourMonthlyData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabourMonthlyDataRepository extends JpaRepository<LabourMonthlyData, Long> {
    List<LabourMonthlyData> findByYearAndMonth(Integer year, Integer month);
    Optional<LabourMonthlyData> findByLabourAndYearAndMonth(Labour labour, Integer year, Integer month);
    void deleteByLabour(Labour labour);
}
