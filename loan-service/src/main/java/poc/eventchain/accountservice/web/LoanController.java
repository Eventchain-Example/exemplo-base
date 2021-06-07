package poc.eventchain.accountservice.web;

import static org.springframework.http.ResponseEntity.ok;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import poc.eventchain.accountservice.model.Loan;
import poc.eventchain.accountservice.model.LoanRepository;
import poc.eventchain.accountservice.model.LoanStatus;
import poc.eventchain.accountservice.service.LoanService;
import poc.eventchain.accountservice.web.request.LoanRequest;
import poc.eventchain.accountservice.web.request.UpdateLoanStatusRequest;

@RestController
@RequestMapping("/v1/loan")
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;

    private final LoanRepository loanRepository;

    @PostMapping("/request")
    public ResponseEntity<Loan> request(@RequestHeader(value = "Authorization") String authorization,
                                        @RequestBody @Validated LoanRequest request) {

        Loan loan = Loan.builder()
                .contractNumber(request.getContractNumber())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .interestRate(request.getInterestRate())
                .status(LoanStatus.REQUESTED)
                .taxtId(request.getTaxId())
                .numberOfInstallments(request.getNumberOfInstallments())
                .automaticDebit(Boolean.FALSE)
                .build();

        loan = loanService.request(loan, authorization);
        log.debug("Loan requested: {}", loan);
        return ok(loan);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestHeader(value = "Authorization") String authorization,
                                         @RequestBody  @Validated UpdateLoanStatusRequest request) {
        loanService.updateLoanStatus(request.getContractNumber(), request.getStatus(), authorization);
        log.debug("Loan id {} updated to status", request.getContractNumber(), request.getStatus());
        return ok().build();
    }

    @PutMapping("/automaticDebit")
    public ResponseEntity<Object> enableAutomaticDebit(@Validated @RequestParam(required = true) Long id) {
        loanService.enableAutomaticDebit(id);
        log.debug("Loan id {} registered for automatic debit", id);
        return ok().build();
    }

    @PostMapping("/pay")
    public ResponseEntity<Object> pay(@RequestHeader(value = "Authorization") String authorization,
                                      @Validated @RequestParam(required = true) Long id,
                                      @Validated @RequestParam(required = true, name = "installment") Integer installment) {
        loanService.pay(id, installment, authorization);
        log.debug("Loan id {} installment number {} paid", id, installment);
        return ok().build();
    }
}
