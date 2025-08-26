package com.helloegor03.order_service.controller;

import com.helloegor03.order_service.config.JwtUtil;
import com.helloegor03.order_service.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/buy/{productId}")
    public ResponseEntity<String> buyProduct(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long buyerId = jwtUtil.getUserIdFromToken(token);
            String buyerUsername = jwtUtil.getUsernameFromToken(token);

            Long orderId = orderService.createOrder(productId, buyerId, buyerUsername);
            return ResponseEntity.ok("Order created with id: " + orderId);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to create order: " + e.getMessage());
        }
    }
}
