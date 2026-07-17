package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByTenantIdAndStudentId(UUID tenantId, Long studentId);
    Optional<Invoice> findByIdAndTenantId(Long id, UUID tenantId);
}
