package com.ufanet.pool.controllers;

import com.ufanet.pool.models.Client;
import com.ufanet.pool.models.dto.ClientAddRequest;
import com.ufanet.pool.models.dto.ClientsGetResponse;
import com.ufanet.pool.services.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0/pool/client")
@AllArgsConstructor
public class ClientController {
    private ClientService clientService;

    @GetMapping("/all")
    public List<ClientsGetResponse> getClients() {
        return clientService.getClients();
    }

    @GetMapping("/get")
    public ResponseEntity<?> getClient(@RequestParam Long id) {
        return clientService.getClient(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClient(@RequestBody ClientAddRequest clientAddRequest) {
        return clientService.addClient(clientAddRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateClient(@RequestBody Client client) {
        return clientService.updateClient(client);
    }

}
