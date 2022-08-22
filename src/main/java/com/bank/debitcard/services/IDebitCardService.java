package com.bank.debitcard.services;

import com.bank.debitcard.models.documents.DebitCard;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IDebitCardService {
    Mono<List<DebitCard>> FindAll();
    Mono<DebitCard> Find(String id);
    Mono<DebitCard> Create(DebitCard debitCard);
    Mono<DebitCard> Update(String id, DebitCard debitCard);
    Mono<Object> Delete(String id);
    Mono<List<DebitCard>> findBydebitCard(String debitCardNumber);
}
