package com.bajajfinserv.hiringchallenge.service;

import com.bajajfinserv.hiringchallenge.dto.WebhookRequest;
import com.bajajfinserv.hiringchallenge.dto.WebhookResponse;
import com.bajajfinserv.hiringchallenge.dto.SolutionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChallengeService implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    private static final String WEBHOOK_GENERATE_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String WEBHOOK_TEST_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @Override
    public void run(String... args) throws Exception {
        try {
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null) {
                // Step 2: Solve SQL problem
                String sqlQuery = solveSqlProblem("22BEC0593"); // Using the example regNo
                
                // Step 3: Submit solution
                submitSolution(webhookResponse.getAccessToken(), sqlQuery);
            }
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest("Atharva Gharge", "22BEC0593", "atharvagharge1079@gmail.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                WEBHOOK_GENERATE_URL, 
                HttpMethod.POST, 
                entity, 
                WebhookResponse.class
            );
            
            System.out.println("Webhook generated successfully");
            System.out.println("Webhook URL: " + response.getBody().getWebhook());
            
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            return null;
        }
    }

    private String solveSqlProblem(String regNo) {
        // Extract last two digits
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastTwoDigitsInt = Integer.parseInt(lastTwoDigits);
        
        // Since 47 is odd, we solve Question 1
        String sqlQuery = """
            SELECT 
                p.AMOUNT as SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) as AGE,
                d.DEPARTMENT_NAME
            FROM PAYMENTS p
            JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) != 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1
            """;
            
        System.out.println("SQL Query generated:");
        System.out.println(sqlQuery);
        
        return sqlQuery.trim();
    }

    private void submitSolution(String accessToken, String sqlQuery) {
        try {
            SolutionRequest request = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                WEBHOOK_TEST_URL, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            System.out.println("Solution submitted successfully");
            System.out.println("Response: " + response.getBody());
            
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
