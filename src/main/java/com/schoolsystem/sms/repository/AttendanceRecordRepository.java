package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByTenantIdAndDateAndStudentCurrentClassId(UUID tenantId, LocalDate date, Long classId);

    @Modifying
    @Query("DELETE FROM AttendanceRecord a WHERE a.tenantId = :tenantId AND a.date = :date AND a.student.id IN :studentIds")
    void deleteByTenantIdAndDateAndStudentIdIn(@Param("tenantId") UUID tenantId, @Param("date") LocalDate date, @Param("studentIds") List<Long> studentIds);
}
