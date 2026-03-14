package com.use_management_system.user_management.service;

import com.use_management_system.user_management.entity.Client;
import com.use_management_system.user_management.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(String clientName, String clientLocation) {
        if (clientName == null || clientName.trim().isEmpty()) {
            throw new RuntimeException("clientName is required");
        }

        String normalizedName = clientName.trim();
        if (clientRepository.findByClientName(normalizedName).isPresent()) {
            throw new RuntimeException("Client already exists");
        }

        Client client = new Client();
        client.setClientName(normalizedName);
        client.setClientLocation(clientLocation == null ? null : clientLocation.trim());
        client.setIsEnabled(true);

        return clientRepository.save(client);
    }

    public Client getClientById(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client resolveClient(UUID clientId) {
        if (clientId != null) {
            return getClientById(clientId);
        }
        return getDefaultClient();
    }

    public Client getDefaultClient() {
        return clientRepository.findFirstByIsEnabledTrueOrderByOnBoardedAsc()
                .orElseThrow(() -> new RuntimeException("No enabled client found"));
    }
}
