package com.bank.debitcard.services.impl;

import com.bank.debitcard.models.dao.DebitCardDao;
import com.bank.debitcard.models.documents.DebitCard;
import com.bank.debitcard.services.IDebitCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
public class DebitCardService implements IDebitCardService {

    @Autowired
    private DebitCardDao dao;

    @Override
    public Mono<List<DebitCard>> FindAll() {
        return dao.findAll().collectList();
    }

    @Override
    public Mono<DebitCard> Find(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<DebitCard> Create(DebitCard debitCard) {
        return dao.save(debitCard);
    }

    @Override
    public Mono<DebitCard> Update(String id, DebitCard debitCard) {
        return dao.existsById(id).flatMap(check ->
        {
            if (Boolean.TRUE.equals(check))
            {
                debitCard.setId(id);
                return dao.save(debitCard);
            }
            else
                return Mono.empty();

        });
    }

    @Override
    public Mono<Object> Delete(String id) {
        return dao.existsById(id).flatMap(check -> {
            if (Boolean.TRUE.equals(check))
                return dao.deleteById(id).then(Mono.just(true));
            else
                return Mono.empty();
        });
    }

    @Override
    public Mono<List<DebitCard>> findBydebitCard(String debitCardNumber) {
        return dao.findAll().filter(p -> p.getDebitCardNumber().equals(debitCardNumber)).sort(Comparator.comparing(DebitCard::isMain)).collectList();
    }
}
