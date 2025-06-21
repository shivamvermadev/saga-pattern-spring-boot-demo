package com.appsdeveloperblog.orders.saga;

import com.appsdeveloperblog.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.core.events.OrderCreatedEvents;
import com.appsdeveloperblog.core.types.OrderStatus;
import com.appsdeveloperblog.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"${orders.event.topic.name}"})
public class OrdersSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String productsCommandTopicName;
    private final OrderHistoryService orderHistoryService;

    public OrdersSaga(KafkaTemplate<String, Object> kafkaTemplate,
                      @Value("${products.commands.topic.name}") String productsCommandTopicName, OrderHistoryService orderHistoryService) {
        this.kafkaTemplate = kafkaTemplate;
        this.productsCommandTopicName = productsCommandTopicName;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvents event) {

        ReserveProductCommand reserveProductCommand = new ReserveProductCommand(
                event.getProductId(),
                event.getProductQuantity(),
                event.getOrderId()
        );

        kafkaTemplate.send(productsCommandTopicName, reserveProductCommand);
        orderHistoryService.add(event.getOrderId(), OrderStatus.CREATED);
    }

}
