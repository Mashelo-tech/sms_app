package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
}
