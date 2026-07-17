package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.Invoice;
import com.schoolsystem.sms.model.Payment;
import com.schoolsystem.sms.repository.InvoiceRepository;
import com.schoolsystem.sms.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceServiceImpl implements FinanceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

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

    @Override
    public BigDecimal getOutstandingBalance(UUID tenantId, Long studentId) {
        List<Invoice> invoices = invoiceRepository.findByTenantIdAndStudentId(tenantId, studentId);

        if (invoices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalIssued = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (Invoice invoice : invoices) {
            totalIssued = totalIssued.add(invoice.getTotalAmountIssued());
            totalPaid = totalPaid.add(invoice.getTotalAmountPaid());
        }

        return totalIssued.subtract(totalPaid);
    }

    @Override
    @Transactional
    public void recordPayment(UUID tenantId, Long invoiceId, BigDecimal amount, String verificationCode) {
        Invoice invoice = invoiceRepository.findByIdAndTenantId(invoiceId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found or access denied"));

        Payment payment = Payment.builder()
                .tenantId(tenantId)
                .invoice(invoice)
                .amountPaid(amount)
                .transactionTimestamp(LocalDateTime.now())
                .bankVerificationCode(verificationCode)
                .build();

        paymentRepository.save(payment);

        invoice.setTotalAmountPaid(invoice.getTotalAmountPaid().add(amount));
        invoiceRepository.save(invoice);
    }
}
