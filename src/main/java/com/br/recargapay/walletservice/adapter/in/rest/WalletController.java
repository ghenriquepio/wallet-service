package com.br.recargapay.walletservice.adapter.in.rest;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.DepositFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.OnboardingRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.TransferFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.request.WithdrawFundsRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.*;
import com.br.recargapay.walletservice.application.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final OnboardingUseCase onboardingUseCase;
    private final RetrieveWalletBalanceUseCase retrieveWalletBalanceUseCase;
    private final RetrieveHistoricalWalletBalanceUseCase retrieveHistoricalWalletBalanceUseCase;
    private final DepositFundsUseCase depositFundsUseCase;
    private final WithdrawFundsUseCase withdrawFundsUseCase;
    private final TransferFundsUseCase transferFundsUseCase;

    @Operation(
            summary = "Onboard a new wallet",
            description = "Creates a new person and wallet in a single operation",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Wallet onboarded successfully",
                            content = @Content(schema = @Schema(implementation = OnboardingResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/onboarding")
    public Mono<OnboardingResponse> onboarding(@Valid @RequestBody OnboardingRequest request) {
        return onboardingUseCase.execute(request);
    }

    @Operation(
            summary = "Get wallet balance",
            description = "Retrieves the current balance of a wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Balance retrieved",
                            content = @Content(schema = @Schema(implementation = WalletBalanceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Wallet not found")
            }
    )
    @GetMapping("/{walletId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public Mono<WalletBalanceResponse> getBalance(
            @Parameter(description = "Wallet ID") @PathVariable UUID walletId) {
        return retrieveWalletBalanceUseCase.findBalanceByWalletId(walletId);
    }

    @Operation(
            summary = "Get historical wallet balance",
            description = "Retrieves the wallet balance at a given date",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historical balance retrieved",
                            content = @Content(schema = @Schema(implementation = WalletHistoricalBalanceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Wallet not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid date format")
            }
    )
    @GetMapping("/{walletId}/historical-balance")
    @ResponseStatus(HttpStatus.OK)
    public Mono<WalletHistoricalBalanceResponse> getHistoricalBalance(
            @Parameter(description = "Wallet ID") @PathVariable UUID walletId,
            @Valid @RequestParam("referenceDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Reference date in ISO format") LocalDateTime referenceDate
    ) {
        return retrieveHistoricalWalletBalanceUseCase.findHistoricalBalanceByWalletId(walletId, referenceDate);
    }

    @Operation(
            summary = "Deposit funds",
            description = "Deposits funds into the wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deposit successful",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Wallet not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/{walletId}/deposit")
    public Mono<WalletResponse> depositFunds(
            @Parameter(description = "Wallet ID") @PathVariable UUID walletId,
            @Valid @RequestBody DepositFundsRequest request
    ) {
        return depositFundsUseCase.deposit(walletId, request);
    }

    @Operation(
            summary = "Withdraw funds",
            description = "Withdraws funds from the wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Wallet not found or insufficient funds"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/{walletId}/withdraw")
    public Mono<WalletResponse> withdraw(
            @Parameter(description = "Wallet ID") @PathVariable UUID walletId,
            @Valid @RequestBody WithdrawFundsRequest request) {
        return withdrawFundsUseCase.withdraw(walletId, request);
    }

    @Operation(
            summary = "Transfer funds",
            description = "Transfers funds from one wallet to another",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer successful",
                            content = @Content(schema = @Schema(implementation = TransferResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Source or destination wallet not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or insufficient funds")
            }
    )
    @PostMapping("/{fromWalletId}/transfer")
    public Mono<TransferResponse> transfer(
            @Parameter(description = "Sender wallet ID") @PathVariable UUID fromWalletId,
            @Valid @RequestBody TransferFundsRequest request) {
        return transferFundsUseCase.transferFunds(fromWalletId, request);
    }
}