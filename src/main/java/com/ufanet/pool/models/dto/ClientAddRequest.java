package com.ufanet.pool.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientAddRequest {
    private String name;
    private String phone;
    private String email;
}
