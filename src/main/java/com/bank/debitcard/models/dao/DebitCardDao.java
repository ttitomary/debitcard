package com.bank.debitcard.models.dao;

import com.bank.debitcard.models.documents.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DebitCardDao extends ReactiveMongoRepository <DebitCard, String>{
}
