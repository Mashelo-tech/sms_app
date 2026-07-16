package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStudentId(Long studentId);
}
