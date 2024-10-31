package com.ufanet.pool.models.dto;

import lombok.Data;

@Data
public class ReservationCancelRequest {
    private long clientId;
    private String orderId;
}
