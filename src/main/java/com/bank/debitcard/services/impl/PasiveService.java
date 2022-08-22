package com.bank.debitcard.services.impl;

import com.bank.debitcard.models.utils.ResponsePasive;
import com.bank.debitcard.services.IPasiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PasiveService implements IPasiveService {
    @Autowired
    @Qualifier("getWebClientPasive")
    WebClient webClient;

    @Override
    public Mono<ResponsePasive> getPasive(String idPasive) {
        return webClient.get()
                .uri("/api/pasive/"+ idPasive)
                .retrieve()
                .bodyToMono(ResponsePasive.class);
    }
}
