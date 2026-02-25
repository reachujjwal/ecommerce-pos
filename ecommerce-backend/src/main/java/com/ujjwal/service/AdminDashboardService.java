package com.ujjwal.service;



import com.ujjwal.payload.AdminAnalysis.DashboardSummaryDTO;
import com.ujjwal.payload.AdminAnalysis.StoreRegistrationStatDTO;
import com.ujjwal.payload.AdminAnalysis.StoreStatusDistributionDTO;


import java.util.List;

public interface AdminDashboardService {

    DashboardSummaryDTO getDashboardSummary();

    List<StoreRegistrationStatDTO> getLast7DayRegistrationStats();

    StoreStatusDistributionDTO getStoreStatusDistribution();
}
