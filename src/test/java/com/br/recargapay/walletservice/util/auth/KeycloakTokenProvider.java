package com.br.recargapay.walletservice.util.auth;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

public class KeycloakTokenProvider {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String realm;

    public KeycloakTokenProvider(String keycloakUrl, String realm, String clientId, String clientSecret) {
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = WebClient.builder()
                .baseUrl(keycloakUrl)
                .build();
    }

    public String getAccessToken() {
        return Objects.requireNonNull(webClient.post()
                .uri("/realms/" + realm + "/protocol/openid-connect/token")
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block())
                .access_token;
    }

    private static class TokenResponse {
        public String access_token;
    }
}