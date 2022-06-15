package com.zabzabdoda;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("movie-api")
public record ConfigProperties(String apiKey, String apiUrl) {
}
