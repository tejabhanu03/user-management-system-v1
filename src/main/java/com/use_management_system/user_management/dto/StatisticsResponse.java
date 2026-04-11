package com.use_management_system.user_management.dto;

public class StatisticsResponse {

    private long onboardedClientsCount;
    private long verifiedUsersCount;

    public StatisticsResponse() {
    }

    public StatisticsResponse(long onboardedClientsCount, long verifiedUsersCount) {
        this.onboardedClientsCount = onboardedClientsCount;
        this.verifiedUsersCount = verifiedUsersCount;
    }

    public long getOnboardedClientsCount() {
        return onboardedClientsCount;
    }

    public void setOnboardedClientsCount(long onboardedClientsCount) {
        this.onboardedClientsCount = onboardedClientsCount;
    }

    public long getVerifiedUsersCount() {
        return verifiedUsersCount;
    }

    public void setVerifiedUsersCount(long verifiedUsersCount) {
        this.verifiedUsersCount = verifiedUsersCount;
    }
}
