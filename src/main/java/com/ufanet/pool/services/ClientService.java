package com.ufanet.pool.services;

import com.ufanet.pool.repositoties.ClientRepository;
import com.ufanet.pool.models.Client;
import com.ufanet.pool.models.dto.ClientAddRequest;
import com.ufanet.pool.models.dto.ClientsGetResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientService {
    private ClientRepository clientRepository;


    public List<ClientsGetResponse> getClients() {
        return clientRepository.findAll().stream().map(
                ClientsGetResponse::new
        ).collect(Collectors.toList());

    }

    public ResponseEntity<?> getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent() )
            return ResponseEntity.ok(client.get());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Client not found with id: " + id);
    }

    public ResponseEntity<?> addClient(ClientAddRequest client) {
        try {
            Client savedClient = clientRepository.save(new Client(client));
            return ResponseEntity.ok(savedClient);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateClient(Client client) {
        try {
            if (!clientRepository.existsById(client.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Client doesn't exist");
            }
            Client savedClient = clientRepository.save(client);
            return ResponseEntity.ok(savedClient);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
