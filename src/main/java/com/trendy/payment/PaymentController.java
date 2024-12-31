package com.trendy.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestPayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentType = (String) request.get("paymentType");
            Long orderId = Long.parseLong(request.get("orderId").toString());
            String userId = (String) request.get("userId");
            int amount = Integer.parseInt(request.get("amount").toString());

            String redirectUrl = paymentService.requestPayment(paymentType, orderId, userId, amount);
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> request) {
        try {
            Map<String, Object> result = paymentService.verifyPayment(request.get("imp_uid"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
