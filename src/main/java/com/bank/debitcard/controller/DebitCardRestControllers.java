package com.bank.debitcard.controller;

import com.bank.debitcard.handlers.ResponseHandler;
import com.bank.debitcard.models.dao.DebitCardDao;
import com.bank.debitcard.models.documents.DebitCard;
import com.bank.debitcard.services.IDebitCardService;
import com.bank.debitcard.services.IPasiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/debitCard")
public class DebitCardRestControllers {
    @Autowired
    private IDebitCardService debitCardService;

    @Autowired
    private IPasiveService pasiveService;
    private static final Logger log = LoggerFactory.getLogger(DebitCardRestControllers.class);

    @PostMapping
    public Mono<ResponseEntity<Object>> Create(@Validated @RequestBody DebitCard p) {
        log.info("[INI] Create Debit Card");
        p.setCreatedDate(LocalDateTime.now());

        return pasiveService.getPasive(p.getPasiveId())
                .flatMap(responsePasive -> {
                    if(responsePasive.getData() != null){
                        log.info(responsePasive.toString());
                        return debitCardService.findBydebitCard(p.getDebitCardNumber())
                                .flatMap(debitCards -> {
                                    if(debitCards != null){
                                        boolean main = debitCards.stream().anyMatch(x->x.isMain() == true);
                                        String pasiveId = debitCards.stream().findFirst().get().getPasiveId();
                                        if(main == p.isMain()){
                                            return Mono.just(ResponseHandler.response("Main account already exists", HttpStatus.NOT_FOUND, null));
                                        }
                                        else{
                                            return pasiveService.getPasive(pasiveId)
                                                    .flatMap(response -> {
                                                        if(response.getData() != null){
                                                            if(responsePasive.getData().getClientId() == response.getData().getClientId())
                                                                return debitCardService.Create(p)
                                                                        .flatMap(debitCard -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, debitCard)));
                                                            else
                                                                return Mono.just(ResponseHandler.response("Client not exists for pasive", HttpStatus.NOT_FOUND, null));
                                                        }
                                                        else
                                                            return Mono.just(ResponseHandler.response("Pasive not found", HttpStatus.NOT_FOUND, null));
                                                        });
                                        }
                                    }
                                    else {
                                        return debitCardService.Create(p)
                                                .flatMap(debitCard -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, debitCard)));
                                    }
                                });
                    }
                    else
                        return Mono.just(ResponseHandler.response("Pasive not found", HttpStatus.NOT_FOUND, null));
                })
                .doFinally(fin -> log.info("[END] Create Debit Card"));
    }

    @GetMapping
    public Mono<ResponseEntity<Object>> FindAll() {
        log.info("[INI] FindAll Debit Card");
        return debitCardService.FindAll()
                .flatMap(debitCard -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, debitCard)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] FindAll Debit Card"));

    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> Find(@PathVariable String id) {
        log.info("[INI] Find Debit Card");
        return debitCardService.Find(id)
                .doOnNext(debitCard -> log.info(debitCard.toString()))
                .map(debitCard -> ResponseHandler.response("Done", HttpStatus.OK, debitCard))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .doFinally(fin -> log.info("[END] Find Debit Card"));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Object>> Update(@PathVariable("id") String id,@Validated @RequestBody DebitCard p) {
        log.info("[INI] Update Debit Card");
        return debitCardService.Update(id,p)
                .flatMap(debitCard -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, debitCard)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Update Pasive"));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> Delete(@PathVariable("id") String id) {
        log.info("[INI] Delete Debit Card");
        log.info(id);

        return debitCardService.Delete(id)
                .flatMap(o -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, null)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Error", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Delete Debit Card"));
    }

    @GetMapping("/debit/{id}")
    public Mono<ResponseEntity<Object>> FindBydebitCard(@PathVariable String debitCardNumber) {
        log.info("[INI] Find by Debit Card");
        return debitCardService.findBydebitCard(debitCardNumber)
                .flatMap(debitCard -> Mono.just(ResponseHandler.response("Done", HttpStatus.OK, debitCard)))
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find by Debit Card"));

    }

    @GetMapping("/mont/{id}")
    public Mono<ResponseEntity<Object>> GetMontdebitCard(@PathVariable String debitCardNumber) {
        log.info("[INI] Find by Debit Card");
        return debitCardService.findBydebitCard(debitCardNumber)
                .flatMap(debitCard -> {
                    Optional<DebitCard> existMain = debitCard.stream()
                            .filter(active -> active.isMain())
                            .findFirst();

                    if(existMain.isPresent()){
                        return pasiveService.getPasive(existMain.get().getPasiveId())
                                .flatMap(responsePasive -> {
                                    if(responsePasive.getData() != null){
                                        return Mono.just(ResponseHandler.response("Done", HttpStatus.NO_CONTENT, responsePasive.getData().getMont()));
                                    }
                                    else{
                                        return Mono.just(ResponseHandler.response("Not exists", HttpStatus.NO_CONTENT, null));
                                    }
                                });
                    }
                    else
                        return Mono.just(ResponseHandler.response("Not exists", HttpStatus.NO_CONTENT, null));
                })
                .onErrorResume(error -> Mono.just(ResponseHandler.response(error.getMessage(), HttpStatus.BAD_REQUEST, null)))
                .switchIfEmpty(Mono.just(ResponseHandler.response("Empty", HttpStatus.NO_CONTENT, null)))
                .doFinally(fin -> log.info("[END] Find by Debit Card"));

    }
}
