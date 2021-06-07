package poc.eventchain.accountservice.web;

import static org.springframework.http.ResponseEntity.ok;

import java.math.BigDecimal;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import poc.eventchain.accountservice.model.Account;
import poc.eventchain.accountservice.service.AccountService;
import poc.eventchain.commons.security.jwt.JwtTokenProvider;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/v1/event")
@RequiredArgsConstructor
@Slf4j
public class EventResolverController {

    @Autowired
    private Gateway gateway;

    private Network network;

    @Value("${event-resolver.blockchain.network}")
    private String networkName;

    @PostConstruct
    public void setup() {
        network = gateway.getNetwork(networkName);
    }

    @GetMapping
    public ResponseEntity<Account> getAll() {
        try {
            // get the network and contract
            Contract contract = network.getContract("event-chaincode");

            byte[] result;

            log.debug("get all transfers from blockchain");
            result = contract.evaluateTransaction("GetAll");
            log.debug(new String(result));
            log.debug("---");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok().build();
    }
}
