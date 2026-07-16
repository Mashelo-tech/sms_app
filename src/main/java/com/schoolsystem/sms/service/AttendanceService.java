package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.AttendanceStatus;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface AttendanceService {
    void saveBatchAttendance(UUID tenantId, LocalDate date, Map<Long, AttendanceStatus> studentStatusMap);
}
