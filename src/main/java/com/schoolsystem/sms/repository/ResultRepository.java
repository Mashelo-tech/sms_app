package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Result;
import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.model.Term;
import com.schoolsystem.sms.model.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByStudentAndTerm(Student student, Term term);
    List<Result> findByTermAndStatus(Term term, ResultStatus status);
}
