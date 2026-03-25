package com.example.MCP;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class McpController {

    @GetMapping("/mcp")
    public Map<String, Object> getTools() {

        Map<String, Object> response = new HashMap<>();

        response.put("status", "MCP Server Running ✅");
        response.put("tools", "AVAILABLE");
        response.put("message", "Salesforce can now connect");

        return response;
    }
}