package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.Invoice;
import com.schoolsystem.sms.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public BigDecimal getOutstandingBalance(Long studentId) {
        List<Invoice> invoices = invoiceRepository.findByStudentId(studentId);

        if (invoices.isEmpty()) {
            throw new IllegalArgumentException("No invoices found for student ID: " + studentId);
        }

        BigDecimal totalIssued = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (Invoice invoice : invoices) {
            totalIssued = totalIssued.add(invoice.getTotalAmountIssued());
            totalPaid = totalPaid.add(invoice.getTotalAmountPaid());
        }

        return totalIssued.subtract(totalPaid);
    }
}
