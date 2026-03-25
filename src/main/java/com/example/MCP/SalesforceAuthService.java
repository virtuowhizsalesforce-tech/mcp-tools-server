package com.example.MCP;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import java.util.Map;

@Service
public class SalesforceAuthService {

    private static final String TOKEN_URL = "https://login.salesforce.com/services/oauth2/token";

    private final RestClient restClient = RestClient.create();

    private String accessToken;
    private String instanceUrl;

    // ✅ Read from application.properties
    @Value("${CLIENT_ID:NOT_FOUND}")
    private String clientId;

    @Value("${CLIENT_SECRET:NOT_FOUND}")
    private String clientSecret;

    @Value("${SF_USERNAME:NOT_FOUND}")
    private String username;

    @Value("${PASSWORD_AND_TOKEN:NOT_FOUND}")
    private String passwordAndToken;

    // ✅ PRINT ON START (VERY IMPORTANT)
    @PostConstruct
    public void printConfig() {
        System.out.println("===== CONFIG CHECK =====");
        System.out.println("CLIENT_ID=" + clientId);
        System.out.println("CLIENT_SECRET=" + clientSecret);
        System.out.println("USERNAME=" + username);
        System.out.println("PASSWORD=" + passwordAndToken);
        System.out.println("========================");
    }

    // ✅ Authenticate
    private synchronized void authenticate() {
        try {

            System.out.println("🔥 AUTH CALLED");

            Map<String, Object> response = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(
                            "grant_type=password" +
                            "&client_id=" + clientId +
                            "&client_secret=" + clientSecret +
                            "&username=" + username +
                            "&password=" + passwordAndToken
                    )
                    .retrieve()
                    .body(Map.class);

            this.accessToken = (String) response.get("access_token");
            this.instanceUrl = (String) response.get("instance_url");

        } catch (Exception e) {
            throw new RuntimeException("❌ Salesforce Auth Failed: " + e.getMessage());
        }
    }

    public String getAccessToken() {
        if (accessToken == null) {
            authenticate();
        }
        return accessToken;
    }

    public String getInstanceUrl() {
        if (instanceUrl == null) {
            authenticate();
        }
        return instanceUrl;
    }
}