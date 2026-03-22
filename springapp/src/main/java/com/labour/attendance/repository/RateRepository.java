// repository/RateRepository.java
package com.labour.attendance.repository;

import com.labour.attendance.entity.Rate;
import com.labour.attendance.entity.Labour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findFirstByLabourAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Labour labour, LocalDate date);
    void deleteByLabour(Labour labour);
}
