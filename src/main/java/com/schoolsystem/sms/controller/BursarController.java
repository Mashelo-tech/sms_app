package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.service.FinanceService;
import com.schoolsystem.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BursarController {

    private final FinanceService financeService;
    private final UserService userService;

    @GetMapping("/bursar/ledger")
    public String viewLedgerRedirect() {
        return "redirect:/dashboard/secretary";
    }

    @PostMapping("/bursar/payment/record")
    public String recordPayment(@RequestParam Long invoiceId,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String verificationCode,
                                Authentication authentication) {

        UUID tenantId = userService.findByUsername(authentication.getName())
                .map(com.schoolsystem.sms.model.User::getTenantId)
                .orElseThrow(() -> new IllegalStateException("Current user has no tenant assigned."));

        try {
            financeService.recordPayment(tenantId, invoiceId, amount, verificationCode);
            return "redirect:/dashboard/secretary?paymentSuccess=Payment+recorded+successfully";
        } catch (Exception e) {
            return "redirect:/dashboard/secretary?error=Failed+to+record+payment";
        }
    }
}
