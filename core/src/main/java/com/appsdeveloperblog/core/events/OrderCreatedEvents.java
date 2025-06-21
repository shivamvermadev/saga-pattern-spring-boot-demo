package com.appsdeveloperblog.core.events;

import java.util.UUID;

public class OrderCreatedEvents {
    private UUID orderId;
    private UUID customerId;
    private UUID productId;
    private int productQuantity;

    public OrderCreatedEvents() {
    }

    public OrderCreatedEvents(UUID orderId, UUID customerId, UUID productId, int productQuantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
