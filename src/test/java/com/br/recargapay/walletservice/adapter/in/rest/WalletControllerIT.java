package com.br.recargapay.walletservice.adapter.in.rest;

import com.br.recargapay.walletservice.WalletServiceApplication;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.DepositFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.OnboardingRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.TransferFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.WithdrawFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.OnboardingResponse;
import com.br.recargapay.walletservice.application.port.out.PersonRepository;
import com.br.recargapay.walletservice.application.port.out.TransactionRepository;
import com.br.recargapay.walletservice.application.port.out.TransferRepository;
import com.br.recargapay.walletservice.application.port.out.WalletRepository;
import com.br.recargapay.walletservice.util.auth.KeycloakTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = WalletServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
public class WalletControllerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransferRepository transferRepository;

    private UUID walletIdFrom;
    private UUID walletIdTo;

    private String accessToken;

    private static final String REALM_NAME = "wallet-realm";
    private static final String CLIENT_ID = "wallet-api";
    private static final String CLIENT_SECRET = "A2n6FYArzOcg89T3kbu1iaKbGvgFaTsd";

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("wallet_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static GenericContainer<?> keycloak = new GenericContainer<>("quay.io/keycloak/keycloak:24.0.1")
            .withExposedPorts(8080)
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCommand("start-dev --import-realm")
            .withCopyFileToContainer(
                    org.testcontainers.utility.MountableFile.forClasspathResource("keycloak/wallet-realm.json"),
                    "/opt/keycloak/data/import/wallet-realm.json"
            );

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/wallet_db");
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080) + "/realms/" + REALM_NAME);
    }

    @BeforeEach
    void setup() {
        walletRepository.deleteAll().block();
        personRepository.deleteAll().block();
        transactionRepository.deleteAll().block();
        transferRepository.deleteAll().block();

        waitForKeycloak();

        KeycloakTokenProvider tokenProvider = new KeycloakTokenProvider(
                "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080),
                REALM_NAME,
                CLIENT_ID,
                CLIENT_SECRET
        );
        this.accessToken = tokenProvider.getAccessToken();

        this.webTestClient = webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();

        OnboardingRequest fromRequest = new OnboardingRequest("Alice Silva", "12345678900", "alice@example.com", "11911111111");
        this.walletIdFrom = Objects.requireNonNull(webTestClient.post()
                        .uri("/wallets/onboarding")
                        .body(Mono.just(fromRequest), OnboardingRequest.class)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(OnboardingResponse.class)
                        .returnResult()
                        .getResponseBody())
                .wallet().id();

        OnboardingRequest toRequest = new OnboardingRequest("Bob Santos", "98765432100", "bob@example.com", "11922222222");
        this.walletIdTo = Objects.requireNonNull(webTestClient.post()
                        .uri("/wallets/onboarding")
                        .body(Mono.just(toRequest), OnboardingRequest.class)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(OnboardingResponse.class)
                        .returnResult()
                        .getResponseBody())
                .wallet().id();

        DepositFundsRequest depositRequest = new DepositFundsRequest(BigDecimal.valueOf(200));
        webTestClient.post()
                .uri("/wallets/{walletId}/deposit", walletIdFrom)
                .bodyValue(depositRequest)
                .exchange()
                .expectStatus().isOk();
    }

    private void waitForKeycloak() {
        boolean isReady = false;
        int retries = 10;
        while (!isReady && retries > 0) {
            try {
                WebTestClient.bindToServer()
                        .responseTimeout(Duration.ofSeconds(5))
                        .baseUrl("http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080))
                        .build()
                        .get()
                        .uri("/realms/" + REALM_NAME)
                        .exchange()
                        .expectStatus().is2xxSuccessful();
                isReady = true;
            } catch (Exception e) {
                retries--;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        assertThat(isReady).isTrue();
    }

    @Test
    void shouldOnboardWalletSuccessfully() {
        OnboardingRequest request = new OnboardingRequest("John Doe", "12345678901", "john@example.com", "11999999999");

        webTestClient.post()
                .uri("/wallets/onboarding")
                .body(Mono.just(request), OnboardingRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.person.fullName").isEqualTo("John Doe");
    }

    @Test
    void shouldGetBalanceSuccessfully() {
        webTestClient.get()
                .uri("/wallets/{walletId}/balance", walletIdFrom)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(200);
    }

    @Test
    void shouldDepositSuccessfully() {
        DepositFundsRequest request = new DepositFundsRequest(BigDecimal.valueOf(50));

        webTestClient.post()
                .uri("/wallets/{walletId}/deposit", walletIdFrom)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(250);
    }

    @Test
    void shouldWithdrawSuccessfully() {
        WithdrawFundsRequest request = new WithdrawFundsRequest(BigDecimal.valueOf(30));

        webTestClient.post()
                .uri("/wallets/{walletId}/withdraw", walletIdFrom)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(170);
    }


    @Test
    void shouldTransferSuccessfully() {
        TransferFundsRequest request = new TransferFundsRequest(walletIdTo, BigDecimal.valueOf(100));

        webTestClient.post()
                .uri("/wallets/{fromWalletId}/transfer", walletIdFrom)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transferredAmount").isEqualTo(100);
    }

    @Test
    void shouldFailOnOnboardingWithInvalidData() {
        OnboardingRequest invalidRequest = new OnboardingRequest("", "", "invalid-email", "");

        webTestClient.post()
                .uri("/wallets/onboarding")
                .body(Mono.just(invalidRequest), OnboardingRequest.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailToGetBalanceForNonExistentWallet() {
        UUID nonExistentWalletId = UUID.randomUUID();

        webTestClient.get()
                .uri("/wallets/{walletId}/balance", nonExistentWalletId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldFailToDepositNegativeAmount() {
        DepositFundsRequest invalidRequest = new DepositFundsRequest(BigDecimal.valueOf(-50));

        webTestClient.post()
                .uri("/wallets/{walletId}/deposit", walletIdFrom)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailToWithdrawMoreThanBalance() {
        WithdrawFundsRequest invalidRequest = new WithdrawFundsRequest(BigDecimal.valueOf(500));

        webTestClient.post()
                .uri("/wallets/{walletId}/withdraw", walletIdFrom)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailToTransferWithInsufficientBalance() {
        TransferFundsRequest invalidRequest = new TransferFundsRequest(walletIdTo, BigDecimal.valueOf(300));

        webTestClient.post()
                .uri("/wallets/{fromWalletId}/transfer", walletIdFrom)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldFailToTransferToNonExistentWallet() {
        UUID nonExistentWalletId = UUID.randomUUID();
        TransferFundsRequest invalidRequest = new TransferFundsRequest(nonExistentWalletId, BigDecimal.valueOf(50));

        webTestClient.post()
                .uri("/wallets/{fromWalletId}/transfer", walletIdFrom)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldGetHistoricalBalanceSuccessfully() {
        LocalDateTime now = LocalDateTime.now();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/wallets/{walletId}/historical-balance")
                        .queryParam("referenceDate", now.toString())
                        .build(walletIdFrom))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(200);
    }

}