package com.ufanet.pool.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ReservationByNameRequest {
    private Long clientId;
    private String name;
    private LocalDate date;
    private LocalTime time;
}
