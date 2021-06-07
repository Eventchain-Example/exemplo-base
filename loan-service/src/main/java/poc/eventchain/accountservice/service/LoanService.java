package poc.eventchain.accountservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import poc.eventchain.accountservice.event.GenericEvent;
import poc.eventchain.accountservice.event.LoanEvent;
import poc.eventchain.accountservice.event.LoanPaymentEvent;
import poc.eventchain.accountservice.event.PaymentType;
import poc.eventchain.accountservice.model.Installment;
import poc.eventchain.accountservice.model.Loan;
import poc.eventchain.accountservice.model.LoanRepository;
import poc.eventchain.accountservice.model.LoanStatus;
import poc.eventchain.accountservice.pojo.Account;
import poc.eventchain.accountservice.web.BusinessEventChainException;
import poc.eventchain.accountservice.web.EventChainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class LoanService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private LoanRepository loanRepository;

    @Value("${loan.topic.loan}")
    private String loanEventTopic;

    @Value("${loan.topic.payment}")
    private String loanPaymentTopic;

    @Value("${account.urlFindByTaxId}")
    private String urlFindByTaxId;

    @Value("${account.urlRoot}")
    private String urlRoott;

    @Autowired
    RestTemplate restTemplate;

    public Loan request(Loan loan, String authorization) {
        findByTaxId(loan.getTaxtId(), authorization).orElseThrow(
                () -> new BusinessEventChainException("Account for taxId not found")
        );

        loanRepository.findByContractNumber(loan.getContractNumber()).ifPresent(
            l -> {
                throw new BusinessEventChainException("Contract Number already in use");
            });

        loanRepository.save(loan);
        sendEvent(generateLoanEvent(loan), loanEventTopic);
        return loan;
    }

    public void updateLoanStatus(Integer contractNumber, LoanStatus loanStatus, String authorization) {
        if (LoanStatus.REQUESTED.equals(loanStatus))
            throw new BusinessEventChainException("Status not allowed");

        Optional<Loan> loanOptional = loanRepository.findByContractNumber(contractNumber);
        Loan loan = loanOptional.orElseThrow(() -> new BusinessEventChainException("Loan not found"));

        if (!loan.getStatus().equals(LoanStatus.REQUESTED))
            throw new BusinessEventChainException("Loan already updated");

        try {
            if (LoanStatus.APPROVED.equals(loanStatus)) {
                loan.setInstallments(generateInstallments(loan));
            }
            loan.setStatus(loanStatus);
            loanRepository.save(loan);
            updateBalance(loan.getTaxtId(), loan.getAmount(), true, authorization);
            sendEvent(generateLoanEvent(loan), loanEventTopic);
        } catch (BusinessEventChainException exception) {
            loan.setStatus(LoanStatus.REQUESTED);
            if (loanStatus.equals(LoanStatus.APPROVED))
                loan.setInstallments(null);

            loanRepository.save(loan);
            throw exception;
        } catch (Exception exception) {
            log.error("Error to update loan: {}", exception.getMessage(), exception);
            throw new EventChainException("Error to update loan: " + exception.getMessage());
        }
    }

    public void pay(Long id, Integer installmentNumber, String authorization) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new BusinessEventChainException("Loan not found"));
        try {
            Installment installment = loan.getInstallments().stream()
                    .filter(i -> i.getInstallmentNumber().equals(installmentNumber))
                    .findFirst().get();
            installment.setSettled(true);
            loanRepository.save(loan);
            updateBalance(loan.getTaxtId(), loan.getAmount(), false, authorization);
            sendEvent(generateLoanPaymentEvent(loan, installment), loanPaymentTopic);
            ;
        } catch (BusinessEventChainException exception) {
            loan.getInstallments().stream()
                    .filter(i -> i.getInstallmentNumber().equals(installmentNumber))
                    .findFirst().get()
                    .setSettled(false);
            loanRepository.save(loan);
            throw exception;
        }
    }

    public void enableAutomaticDebit(Long id) {
        // find in the database
        Optional<Loan> loanOptional = loanRepository.findById(id);
        Loan loan = loanOptional.orElseThrow(() -> new BusinessEventChainException("Loan not found"));

        if (!LoanStatus.APPROVED.equals(loan.getStatus()))
            throw new BusinessEventChainException("The loan status does not allow automatic debit");

        // update state
        loan.setAutomaticDebit(true);
        loanRepository.save(loan);
        // TODO verificar a habilitação do débito automático
        //sendMessagage(loan);
    }

//    public void sendMessage(Loan message) {
//        sendMessagage(message);
//    }

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

    private LoanEvent generateLoanEvent(Loan loan) {
        LoanEvent loanEvent = LoanEvent.builder()
                .id(UUID.randomUUID().toString())
                .taxId(loan.getTaxtId())
                .interestRate(loan.getInterestRate())
                .eventType(loan.getStatus())
                .createDate(LocalDateTime.now())
                .version(1)
                .build();
        BeanUtils.copyProperties(loan, loanEvent, "id", "status", "version");
        return loanEvent;
    }

    private LoanPaymentEvent generateLoanPaymentEvent(Loan loan, Installment installment) {
        LoanPaymentEvent loanPaymentEvent = LoanPaymentEvent.builder()
                .id(UUID.randomUUID().toString())
                .createDate(LocalDateTime.now())
                .eventType(PaymentType.IN_TIME)
                .version(1)
                .numberOfInstallments(loan.getNumberOfInstallments())
                .taxId(loan.getTaxtId())
                .build();
        BeanUtils.copyProperties(loanPaymentEvent, installment, "id", "version");

        if (installment.getDueDate().isBefore(LocalDate.now())) {
            loanPaymentEvent.setEventType(PaymentType.WITH_DELAY);
        }

        return loanPaymentEvent;
    }

    private Set<Installment> generateInstallments(Loan loan) {
        Set<Installment> installments = new HashSet<>();
        BigDecimal swearAmount = loan.getInterestRate().divide(new BigDecimal(100)).multiply(loan.getAmount());
        BigDecimal installmentAmount = loan.getAmount().add(swearAmount);
        LocalDate localDate = LocalDate.now();
        localDate.withDayOfMonth(loan.getDueDate());

        // First installment always for the next month
        for (int i = 1; i <= loan.getNumberOfInstallments(); i++) {
            localDate = localDate.plusMonths(1l);
            installments.add(new Installment(null, loan, i, false, installmentAmount, localDate));
        }
        return installments;
    }

    private Optional<Account> findByTaxId(String taxId, String authorization) {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        httpHeaders.add("Authorization", authorization);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(urlFindByTaxId)
                .queryParam("taxId", taxId);

        try{
            ResponseEntity<Account> responseEntity = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, httpEntity, Account.class);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK))
                return Optional.of(responseEntity.getBody());
        } catch (HttpClientErrorException httpClientErrorException) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private void updateBalance(String taxId, BigDecimal amount, boolean credit, String authorization) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, Object> parameters = new HashMap<>();
        HttpEntity httpEntity = new HttpEntity<>(parameters, httpHeaders);
        httpHeaders.add("Authorization", authorization);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        parameters.put("taxId", taxId);
        parameters.put("amount", amount);
        parameters.put("receiptNumber", UUID.randomUUID().toString());
        String url;

        if (credit) {
            url = urlRoott.concat("/credit");
        } else {
            url = urlRoott.concat("/debit");
        }

        ResponseEntity<?> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST, httpEntity, Void.class);
        if (!responseEntity.getStatusCode().equals(HttpStatus.OK))
            throw new EventChainException("Was not possible credit/debit the amount");
    }
}
