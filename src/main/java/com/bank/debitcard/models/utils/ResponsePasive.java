package com.bank.debitcard.models.utils;

import com.bank.debitcard.models.documents.Pasive;
import lombok.Data;

@Data
public class ResponsePasive {
    private Pasive data;

    private String message;

    private String status;
}
