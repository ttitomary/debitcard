package com.bank.debitcard.services;

import com.bank.debitcard.models.utils.ResponsePasive;
import reactor.core.publisher.Mono;

public interface IPasiveService {
    Mono<ResponsePasive> getPasive(String idPasive);
}
