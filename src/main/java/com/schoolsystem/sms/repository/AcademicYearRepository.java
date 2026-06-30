package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.AcademicYear;
import com.schoolsystem.sms.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByActiveTrue();
    Optional<AcademicYear> findByName(String name);
}
