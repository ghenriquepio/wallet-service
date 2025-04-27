package com.br.recargapay.walletservice.application.usecase;

import com.br.recargapay.walletservice.adapter.in.rest.dto.request.OnboardingRequest;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.OnboardingResponse;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.PersonResponse;
import com.br.recargapay.walletservice.adapter.in.rest.dto.response.WalletResponse;
import com.br.recargapay.walletservice.application.port.in.ConsultPersonUseCase;
import com.br.recargapay.walletservice.application.port.in.CreatePersonUseCase;
import com.br.recargapay.walletservice.application.port.in.CreateWalletUseCase;
import com.br.recargapay.walletservice.application.port.in.OnboardingUseCase;
import com.br.recargapay.walletservice.domain.exception.PersonExistsException;
import com.br.recargapay.walletservice.domain.model.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static com.br.recargapay.walletservice.domain.exception.ExceptionEnum.PERSON_EXISTS;

@Service
@RequiredArgsConstructor
public class OnboardingService implements OnboardingUseCase {

    private final CreatePersonUseCase createPersonUseCase;
    private final CreateWalletUseCase createWalletUseCase;
    private final ConsultPersonUseCase consultPersonUseCase;

    @Override
    @Transactional
    public Mono<OnboardingResponse> execute(OnboardingRequest request) {
        Person person = request.toDomain();

        return consultPersonUseCase.existsByDocument(person.getDocumentNumber())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new PersonExistsException(PERSON_EXISTS.getMessage(), PERSON_EXISTS.getStatusCode()));
                    }

                    return createPersonUseCase.save(person)
                            .flatMap(savedPerson ->
                                    createWalletUseCase.create(savedPerson.getId())
                                            .map(savedWallet ->
                                                    new OnboardingResponse(
                                                            PersonResponse.fromDomain(savedPerson),
                                                            WalletResponse.fromDomain(savedWallet)
                                                    )
                                            )
                            );
                });
    }
}