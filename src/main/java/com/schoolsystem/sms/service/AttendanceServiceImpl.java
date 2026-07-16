package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.AttendanceRecord;
import com.schoolsystem.sms.model.AttendanceStatus;
import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.repository.AttendanceRecordRepository;
import com.schoolsystem.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void saveBatchAttendance(UUID tenantId, LocalDate date, Map<Long, AttendanceStatus> studentStatusMap) {
        List<AttendanceRecord> records = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Long, AttendanceStatus> entry : studentStatusMap.entrySet()) {
            Long studentId = entry.getKey();
            AttendanceStatus status = entry.getValue();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

            // Validate that the student belongs to the requested tenant
            if (!student.getTenantId().equals(tenantId)) {
                throw new IllegalArgumentException("Student ID " + studentId + " does not belong to the specified tenant.");
            }

            AttendanceRecord record = AttendanceRecord.builder()
                    .tenantId(tenantId)
                    .student(student)
                    .date(date)
                    .status(status)
                    .recordTimestamp(now)
                    .build();

            records.add(record);
        }

        attendanceRecordRepository.saveAll(records);
    }
}
