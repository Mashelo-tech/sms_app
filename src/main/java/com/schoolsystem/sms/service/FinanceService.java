package com.schoolsystem.sms.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface FinanceService {
    BigDecimal getOutstandingBalance(Long studentId);
    BigDecimal getOutstandingBalance(UUID tenantId, Long studentId);
    void recordPayment(UUID tenantId, Long invoiceId, BigDecimal amount, String verificationCode);
}
