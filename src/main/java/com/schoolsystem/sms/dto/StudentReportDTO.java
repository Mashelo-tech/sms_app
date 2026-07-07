package com.schoolsystem.sms.dto;

import com.schoolsystem.sms.model.Result;
import com.schoolsystem.sms.model.Student;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StudentReportDTO {
    private Student student;
    private List<Result> results;
    private double totalMarks;
    private double averageMarks;
    private int position; // Rank in class
    private String division; // Division 1, 2 etc.
    private int totalPoints; // Sum of all subject points (aggregate)
}
