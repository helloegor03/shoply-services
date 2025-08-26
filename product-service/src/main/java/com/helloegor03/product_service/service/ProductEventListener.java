package com.helloegor03.product_service.service;

import com.helloegor03.product_service.dto.OrderCreatedEvent;
import com.helloegor03.product_service.dto.ProductReservedEvent;
import com.helloegor03.product_service.product.ProductStatus;
import com.helloegor03.product_service.repository.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventListener {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductEventListener(ProductRepository productRepository,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "product-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        productRepository.findById(event.getProductId()).ifPresentOrElse(product -> {
            if (product.getStatus() == ProductStatus.AVAILABLE) {
                product.setStatus(ProductStatus.SOLD);
                productRepository.save(product);

                ProductReservedEvent reservedEvent = new ProductReservedEvent();
                reservedEvent.setProductId(product.getId());
                reservedEvent.setOrderId(event.getOrderId());
                reservedEvent.setSellerId(product.getUserId());
                reservedEvent.setStatus("RESERVED");
                kafkaTemplate.send("product-events", reservedEvent);

            } else {
                ProductReservedEvent failedEvent = new ProductReservedEvent();
                failedEvent.setProductId(product.getId());
                failedEvent.setOrderId(event.getOrderId());
                failedEvent.setSellerId(product.getUserId());
                failedEvent.setStatus("FAILED");
                kafkaTemplate.send("product-events", failedEvent);
            }
        }, () -> {
            // the product is not found
            ProductReservedEvent failedEvent = new ProductReservedEvent();
            failedEvent.setProductId(event.getProductId());
            failedEvent.setOrderId(event.getOrderId());
            failedEvent.setSellerId(null);
            failedEvent.setStatus("FAILED");
            kafkaTemplate.send("product-events", failedEvent);
        });
    }
}