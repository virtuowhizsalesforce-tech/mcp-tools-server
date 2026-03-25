package com.example.MCP;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SalesforceService {

    private final RestClient restClient;
    private final SalesforceAuthService authService;

    public SalesforceService(SalesforceAuthService authService) {
        this.authService = authService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Creates a Salesforce Permission Set using Metadata API. Provide API name and label.")
    public String createPermissionSet(
            @ToolParam(description = "API name of the Permission Set (e.g., My_Custom_PS)") String psName,
            @ToolParam(description = "Label for the Permission Set") String psLabel
    ) {

        try {
            String accessToken = authService.getAccessToken();
            String instanceUrl = authService.getInstanceUrl() + "/services/Soap/m/64.0";

            String body =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <env:Header>\n" +
                    "    <urn:SessionHeader xmlns:urn=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "      <urn:sessionId>" + accessToken + "</urn:sessionId>\n" +
                    "    </urn:SessionHeader>\n" +
                    "  </env:Header>\n" +
                    "  <env:Body>\n" +
                    "    <createMetadata xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n" +
                    "      <metadata xsi:type=\"PermissionSet\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                    "        <fullName>" + psName + "</fullName>\n" +
                    "        <label>" + psLabel + "</label>\n" +
                    "        <userPermissions>\n" +
                    "          <enabled>true</enabled>\n" +
                    "          <name>ApiEnabled</name>\n" +
                    "        </userPermissions>\n" +
                    "      </metadata>\n" +
                    "    </createMetadata>\n" +
                    "  </env:Body>\n" +
                    "</env:Envelope>";

            ResponseEntity<String> response = restClient.post()
                    .uri(instanceUrl)
                    .body(body)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                    .header("SOAPAction", "\"\"")
                    .retrieve()
                    .toEntity(String.class);

            return "✅ Permission Set Created: " + response.getBody();

        } catch (Exception e) {
            return "❌ Error creating Permission Set: " + e.getMessage();
        }
    }
}