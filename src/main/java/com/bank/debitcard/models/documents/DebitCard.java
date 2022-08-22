package com.bank.debitcard.models.documents;

import com.bank.debitcard.models.utils.Audit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "debitcards")
public class DebitCard extends Audit {
    @Id
    private String id;
    @NotNull(message = "pasiveId must not be null")
    private String pasiveId;
    @NotNull(message = "debitCardNumber must not be null")
    private String debitCardNumber;
    private boolean main;
}
