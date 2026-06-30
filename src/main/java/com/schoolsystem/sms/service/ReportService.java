package com.schoolsystem.sms.service;

import com.schoolsystem.sms.dto.StudentReportDTO;
import java.util.List;

public interface ReportService {
    List<StudentReportDTO> generateClassBroadsheet(Long classLevelId, Long termId);
    StudentReportDTO generateStudentReport(Long studentId, Long termId);
}
