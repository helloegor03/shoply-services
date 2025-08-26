package com.helloegor03.order_service.service;

import com.helloegor03.order_service.dto.OrderCreatedEvent;
import com.helloegor03.order_service.model.Order;
import com.helloegor03.order_service.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    public OrderService(KafkaTemplate<String, Object> kafkaTemplate, OrderRepository orderRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this. orderRepository = orderRepository;
    }

    public Long createOrder(Long productId, Long buyerId, String buyerUsername) {
        Order order = new Order();
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setBuyerUsername(buyerUsername);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);


        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getId());
        event.setProductId(productId);
        event.setBuyerId(buyerId);
        event.setBuyerUsername(buyerUsername);
        kafkaTemplate.send("order-events", event);

        return savedOrder.getId();
    }
}
