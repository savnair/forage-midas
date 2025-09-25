package com.jpmc.midascore.service;


import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.jpmc.midascore.Incentive;

@Service
public class IncentiveService {

    private final RestTemplate restTemplate;
    private final String incentiveAPI = "http://localhost:8080/incentive";

    public IncentiveService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public double getIncentive (Transaction transaction){
        try{
            ResponseEntity<Incentive> response = restTemplate.postForEntity(
              incentiveAPI,
              transaction,
              Incentive.class
            );
            return response.getBody().getAmount();
        } catch (Exception e) {
            System.out.println("Incentive API Error: " + e.getMessage());
            return 0.0;
        }
    }

}
