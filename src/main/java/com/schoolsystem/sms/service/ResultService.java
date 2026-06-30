package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.Result;
import com.schoolsystem.sms.model.ResultStatus;
import com.schoolsystem.sms.model.User;
import java.util.List;

public interface ResultService {
    Result saveResult(Long studentId, Long subjectId, Long termId, double marks, String username);
    List<Result> submitResults(List<Long> resultIds, String username); // Teacher submits batch
    List<Result> approveResults(List<Long> resultIds, String username); // DOS approves batch
    List<Result> returnResults(List<Long> resultIds, String username); // DOS returns batch
    
    // Auto-calculate
    void calculateGradeAndPoints(Result result);
}
