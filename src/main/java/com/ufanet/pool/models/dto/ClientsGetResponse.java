package com.ufanet.pool.models.dto;

import com.ufanet.pool.models.Client;
import lombok.Data;

@Data
public class ClientsGetResponse {
    private Long id;
    private String name;
    public ClientsGetResponse(Client client) {
        id = client.getId();
        name = client.getName();
    }
}
