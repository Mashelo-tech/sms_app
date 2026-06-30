package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {
    
    @Query("SELECT g FROM GradingScale g WHERE :mark BETWEEN g.minMark AND g.maxMark")
    Optional<GradingScale> findByMark(int mark);
}
