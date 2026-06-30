package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.ClassLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClassLevelRepository extends JpaRepository<ClassLevel, Long> {
    Optional<ClassLevel> findByName(String name);
}
