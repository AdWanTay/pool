package com.ufanet.pool.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class RecordGetAllResponse {
    private LocalTime time;
    private Long count;
}
