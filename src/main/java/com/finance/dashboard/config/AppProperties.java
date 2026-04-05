package com.finance.dashboard.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.seed")
public class AppProperties {

    private boolean enabled;
    private String adminUsername;
    private String adminPassword;
    private String adminEmail;
}
