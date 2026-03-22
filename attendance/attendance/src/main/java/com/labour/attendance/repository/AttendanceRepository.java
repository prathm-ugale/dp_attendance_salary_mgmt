// repository/AttendanceRepository.java
package com.labour.attendance.repository;

import com.labour.attendance.dto.AdminAttendanceRowDTO;
import com.labour.attendance.dto.AttendanceSummaryDTO;
import com.labour.attendance.entity.Attendance;
import com.labour.attendance.entity.Labour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByLabourAndDate(Labour labour, LocalDate date);
    List<Attendance> findByLabourAndDateBetween(Labour labour, LocalDate start, LocalDate end);

     @Query("""
        select new com.labour.attendance.dto.AttendanceSummaryDTO(
            a.labour.id,
            a.labour.codeNo,
            a.labour.name,
            a.shiftsWorked,
            a.kharcha1,
            a.kharcha2,
            a.kharcha3,
            a.bhada
        )
        from Attendance a
        where a.date = :date
    """)
    List<AttendanceSummaryDTO> findSummaryByDate(@Param("date") LocalDate date);

    @Query("""
        select new com.labour.attendance.dto.AdminAttendanceRowDTO(
            a.id,
            a.date,
            a.labour.id,
            a.labour.codeNo,
            a.labour.name,
            a.labour.aadhar,
            a.labour.mobile,
            a.labour.perDayMoney,
            a.shiftsWorked,
            a.lossOfPay,
            a.kharcha1,
            a.kharcha2,
            a.kharcha3,
            a.bhada
        )
        from Attendance a
        order by a.date desc, a.labour.codeNo
    """)
    List<AdminAttendanceRowDTO> findAllForAdmin();

    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);

    void deleteByLabour(Labour labour);

}
