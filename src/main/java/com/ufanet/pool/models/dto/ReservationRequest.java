package com.ufanet.pool.models.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private Long clientId;
    private String datetime;
}
