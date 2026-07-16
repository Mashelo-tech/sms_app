package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
}
