package poc.eventchain.accountservice.web;

import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import poc.eventchain.accountservice.model.AccountRepository;
import poc.eventchain.accountservice.service.AccountService;
import poc.eventchain.accountservice.web.request.AccountCreationRequest;
import poc.eventchain.accountservice.web.request.AccountTransactionRequest;
import poc.eventchain.commons.security.jwt.JwtTokenProvider;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final AccountService accountService;

    private final AccountRepository users;

    @PostMapping
    public ResponseEntity create(@Validated @RequestBody AccountCreationRequest request) {
        accountService.create(request.getTaxId(), request.getNumber());
        return ok().build();
    }

    @GetMapping("/byTaxId")
    public ResponseEntity getByTaxId(@Validated @RequestParam(name = "taxId") @Pattern(regexp = "^\\d{3}\\d{3}\\d{3}\\d{2}$", message = "taxId invalid") String taxId) {
        return accountService.findByTaxId(taxId).map(a -> ok(a)).
                orElseGet(() -> notFound().build());
    }

    @PostMapping("/credit")
    public ResponseEntity credit(@Validated @RequestBody AccountTransactionRequest request) {
        accountService.credit(request.getTaxId(), request.getAmount(), request.getReceiptNumber());
        return ok().build();
    }

    @PostMapping("debit")
    public ResponseEntity debit(@Validated @RequestBody AccountTransactionRequest request) {
        accountService.debit(request.getTaxId(), request.getAmount(), request.getReceiptNumber());
        return ok().build();
    }

//    @PostMapping
//    public ResponseEntity<Account> signin(@RequestBody @Validated AccountCreationRequest data) {
//        try {
//            Account account = new Account();
//            for (int index = 0; index < 10; index++) {
//                account = new Account((long) index, "010" + index, "0001", "121", new BigDecimal(new Random().nextInt(10000)), "xiz" + index);
//                accountService.sendMessage(account);
//            }
//            return ok(account);
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid username/password supplied");
//        }
//    }
//
//    @RequestMapping(path = "/teste2", method = RequestMethod.GET)
//    public ResponseEntity<Account> signin(@RequestParam(name = "username") String username) {
//
//        try {
//            Account account = new Account(1l, "0100", "0001", "121", new BigDecimal(10.0), "xiz");
//            return ok(account);
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid username/password supplied");
//        }
//    }
}
