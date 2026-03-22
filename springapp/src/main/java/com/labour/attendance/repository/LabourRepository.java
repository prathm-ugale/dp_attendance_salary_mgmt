// repository/LabourRepository.java
package com.labour.attendance.repository;

import com.labour.attendance.entity.Labour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LabourRepository extends JpaRepository<Labour, Long> {
    List<Labour> findByNameContainingIgnoreCase(String name);
    Labour findByCodeNo(String codeNo);
    boolean existsByCodeNoIgnoreCase(String codeNo);
    boolean existsByCodeNoIgnoreCaseAndIdNot(String codeNo, Long id);
}
