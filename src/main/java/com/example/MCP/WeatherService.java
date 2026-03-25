package com.example.MCP;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private final SalesforceAuthService authService;

    public WeatherService(SalesforceAuthService authService) {
        this.authService = authService;
    }

    @Tool(description = "Get weather alerts for a US state")
    public String getAlerts(
            @ToolParam(description = "Two-letter US state code (e.g. CA, NY)") String state
    ) {
        return "Weather alert service is active for state: " + state;
    }

    @Tool(description = "Create leads in Salesforce")
    public String createlead(
            @ToolParam(description = "First Name of the customer") String FirstName,
            @ToolParam(description = "Last Name of the customer") String LastName,
            @ToolParam(description = "Email of the customer") String Email,
            @ToolParam(description = "Company Name of the customer") String Company
    ) {

        String accessToken = authService.getAccessToken();
        String instanceUrl = authService.getInstanceUrl();

        try {
            String leadUrl = instanceUrl + "/services/data/v57.0/sobjects/Lead/";

            Map<String, String> lead = new HashMap<>();
            lead.put("FirstName", FirstName);
            lead.put("LastName", LastName);
            lead.put("Email", Email);
            lead.put("Company", Company);

            String response = RestClient.create()
                    .post()
                    .uri(leadUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(lead)
                    .retrieve()
                    .body(String.class);

            return "✅ Lead created successfully: " + response;

        } catch (Exception e) {
            return "❌ Error creating lead in Salesforce: " + e.getMessage();
        }
    }
}