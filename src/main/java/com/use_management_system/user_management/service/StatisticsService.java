package com.use_management_system.user_management.service;

import com.use_management_system.user_management.dto.StatisticsResponse;
import com.use_management_system.user_management.repository.ClientRepository;
import com.use_management_system.user_management.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public StatisticsService(ClientRepository clientRepository,
                             UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public StatisticsResponse getOverviewStatistics() {
        long onboardedClientsCount = clientRepository.count();
        long verifiedUsersCount = userRepository.countByEmailVerifiedTrue();

        return new StatisticsResponse(onboardedClientsCount, verifiedUsersCount);
    }
}
