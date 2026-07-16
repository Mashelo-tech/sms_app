package com.schoolsystem.sms.service;

import java.math.BigDecimal;

public interface FinanceService {
    BigDecimal getOutstandingBalance(Long studentId);
}
