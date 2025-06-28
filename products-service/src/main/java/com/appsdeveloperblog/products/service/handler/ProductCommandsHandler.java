package com.appsdeveloperblog.products.service.handler;

import com.appsdeveloperblog.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.core.dto.Product;
import com.appsdeveloperblog.core.events.ProductReservationFailedEvent;
import com.appsdeveloperblog.core.events.ProductReservedEvents;
import com.appsdeveloperblog.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${products.commands.topic.name}")
public class ProductCommandsHandler {

    private final ProductService productService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsEventsTopicName;

    public ProductCommandsHandler(ProductService productService, KafkaTemplate<String, Object> kafkaTemplate,
                                  @Value("${products.events.topic.name}") String productsEventsTopicName) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
        this.productsEventsTopicName = productsEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {
        try {
            Product product = new Product(command.getOrderId(), command.getProductQuantity());
            productService.reserve(product, command.getOrderId());
            ProductReservedEvents productReservedEvents = new ProductReservedEvents(command.getOrderId(),
                    command.getProductId(),
                    command.getProductQuantity(),
                    product.getPrice()
            );
            kafkaTemplate.send(productsEventsTopicName, productReservedEvents);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(
                    command.getProductId(), command.getOrderId(), command.getProductQuantity());
            kafkaTemplate.send(productsEventsTopicName, productReservationFailedEvent);
        }
    }
}
