package com.example.MCP;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class Permissionsetassign {

    private final RestClient restClient;
    private final SalesforceAuthService authService;

    public Permissionsetassign(SalesforceAuthService authService) {
        this.authService = authService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Assign the permission set to the user by using the username and permission name")
    public String assignPermissionSetByName(
            @ToolParam(description = "Username of the user") String username,
            @ToolParam(description = "Permission Set API Name") String permissionSetName) {

        try {
            String accessToken = authService.getAccessToken();
            String instanceUrl = "https://ne1744285445077.my.salesforce.com";

            // 1️⃣ Get User Id
            String userQueryUrl = instanceUrl + "/services/data/v61.0/query?q=" +
                    "SELECT+Id+FROM+User+WHERE+Username='" + username + "'";

            ResponseEntity<Map> userResponse = restClient.get()
                    .uri(userQueryUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toEntity(Map.class);

            String userId = (String) ((Map) ((Map) ((java.util.List)
                    userResponse.getBody().get("records")).get(0))).get("Id");

            // 2️⃣ Get Permission Set Id
            String psQueryUrl = instanceUrl + "/services/data/v61.0/query?q=" +
                    "SELECT+Id+FROM+PermissionSet+WHERE+Name='" + permissionSetName + "'";

            ResponseEntity<Map> psResponse = restClient.get()
                    .uri(psQueryUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toEntity(Map.class);

            String permissionSetId = (String) ((Map) ((Map) ((java.util.List)
                    psResponse.getBody().get("records")).get(0))).get("Id");

            // 3️⃣ Assign Permission Set
            String endpoint = instanceUrl + "/services/data/v61.0/sobjects/PermissionSetAssignment";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("AssigneeId", userId);
            requestBody.put("PermissionSetId", permissionSetId);

            ResponseEntity<String> assignResponse = restClient.post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(String.class);

            return "✅ Permission Set assigned successfully";

        } catch (Exception e) {
            return "❌ Error assigning Permission Set: " + e.getMessage();
        }
    }
}
