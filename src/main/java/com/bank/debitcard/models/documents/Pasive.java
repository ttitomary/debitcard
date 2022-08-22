package com.bank.debitcard.models.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Pasive {
    @Id
    private String id;
    private String clientId;
    private String pasivesType;
    private Float mont;
    //private Date specificDay;
}
