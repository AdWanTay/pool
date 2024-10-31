package com.ufanet.pool.models.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReserveConsistentlyRequest {
    private Long clientId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
