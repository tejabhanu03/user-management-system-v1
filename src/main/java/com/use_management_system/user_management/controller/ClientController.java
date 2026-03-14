package com.use_management_system.user_management.controller;

import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Map<String, String> request) {
        String clientName = request.get("clientName");
        String clientLocation = request.get("clientLocation");

        Client client = clientService.createClient(clientName, clientLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable String clientId) {
        Client client = clientService.getClientById(UUID.fromString(clientId));
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }
}
