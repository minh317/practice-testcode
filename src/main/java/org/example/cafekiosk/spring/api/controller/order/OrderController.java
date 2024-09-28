package org.example.cafekiosk.spring.api.controller.order;

import lombok.RequiredArgsConstructor;
import org.example.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import org.example.cafekiosk.spring.api.serveice.order.OrderService;
import org.example.cafekiosk.spring.api.serveice.order.response.OrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class OrderController {

    final OrderService _orderService;

    @PostMapping("/api/v1/orders/new")
    public OrderResponse createOrder(@RequestBody OrderCreateRequest request) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return _orderService.createOrder(request, registeredDateTime);
    }
}
