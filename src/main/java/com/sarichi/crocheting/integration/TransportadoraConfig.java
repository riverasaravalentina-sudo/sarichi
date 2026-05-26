package com.sarichi.crocheting.integration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.transportadora")
@Data
public class TransportadoraConfig {
    private String apiUrl;
    private String apiKey;
    private boolean mockEnabled;
}
