package poc.eventchain.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import poc.eventchain.accountservice.event.AccountEvent;
import poc.eventchain.accountservice.event.AccountEventType;
import poc.eventchain.accountservice.event.AccountTranctionEvent;
import poc.eventchain.accountservice.event.GenericEvent;
import poc.eventchain.accountservice.model.Account;
import poc.eventchain.accountservice.model.AccountRepository;
import poc.eventchain.accountservice.web.BusinessEventChainException;
import poc.eventchain.accountservice.web.request.AccountTransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class AccountService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${account.topic.event}")
    private String topicName;

    @Value("${account.topic.transaction}")
    private String transactionTopic;

    @Value("${account.branch}")
    private String branch;

    @Value("${account.bank}")
    private String bank;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public void create(String taxId, String number) {
        List<Account> accountsFound = accountRepository.findByTaxIdOrNumber(taxId, number);

        if (accountsFound.size() > 0)
            throw new BusinessEventChainException("TaxId/Number already in use");

        Account account = new Account(null, number, branch, bank, new BigDecimal(0), taxId, 0L);
        account = accountRepository.save(account);
        sendEvent(generateEvent(account, AccountEventType.CREATED), topicName);
        log.debug("Account id: {} bank: {} branch: {} created to taxId: {}", account.getId(), account.getBank(), account.getBranch(), account.getTaxId());
    }

    public Optional<Account> findByTaxId(String taxId) {
        return accountRepository.findByTaxId(taxId);
    }

    public void credit(String taxtId, BigDecimal amount, String receiptNumber) {
        Account account = accountRepository.findByTaxId(taxtId).orElseThrow(() -> new BusinessEventChainException("Account for taxId not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        sendEvent(generateTransationEvent(account, AccountEventType.CREDIT, receiptNumber, amount), transactionTopic);
        log.debug("Account id: {} bank: {} branch: {} received credit in the amount of : {}", account.getId(), account.getBank(), account.getBranch(), amount);
    }

    public void debit(String taxtId, BigDecimal amount, String receiptNumber) {
        Account account = accountRepository.findByTaxId(taxtId).orElseThrow(() -> new BusinessEventChainException("Account for taxId not found"));
        account.setBalance(account.getBalance().subtract(amount));

//        if(account.getBalance().compareTo(BigDecimal.ZERO) < 0)
//            throw new BusinessEventChainException("Account does not have balance to debit");

        accountRepository.save(account);
        sendEvent(generateTransationEvent(account, AccountEventType.DEBIT, receiptNumber, amount), transactionTopic);
        log.debug("Account id: {} bank: {} branch: {} debited in the amount of : {}", account.getId(), account.getBank(), account.getBranch(), amount);
    }

    private void sendEvent(GenericEvent event, String topicName) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, event.getId(), event);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + event + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + event + "] due to : " + ex.getMessage());
            }
        });
    }

    private GenericEvent generateEvent(Account account, AccountEventType eventType) {
        AccountEvent event = AccountEvent.builder()
                .id(UUID.randomUUID().toString())
                .eventType(eventType)
                .createDate(LocalDateTime.now())
                .version(1L)
                .build();
        BeanUtils.copyProperties(account, event, "id", "version");
        return event;
    }

    private GenericEvent generateTransationEvent(Account account, AccountEventType eventType, String receiptNumber, BigDecimal amount) {
        AccountTranctionEvent event = AccountTranctionEvent.builder()
                .id(UUID.randomUUID().toString())
                .eventType(eventType)
                .createDate(LocalDateTime.now())
                .version(1L)
                .receiptNumber(receiptNumber)
                .amount(amount)
                .build();
        BeanUtils.copyProperties(account, event, "id", "version");
        return event;
    }

    public void create(Account account) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, account.getId().toString(), account);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + account + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + account + "] due to : " + ex.getMessage());
            }
        });
    }

    public void enableAutomaticDebit() {
        // gera evento no kafka
    }

    public void sendMessage(Account message) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, message.getId().toString(), message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
    }
}
