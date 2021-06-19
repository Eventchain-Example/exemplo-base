package poc.eventchain.accountservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import poc.eventchain.accountservice.model.Transfer;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class EventResolverConsumer {

    @Autowired
    private Gateway gateway;

    private Network network;

    @Value("${event-resolver.blockchain.network}")
    private String networkName;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void setup() {
        network = gateway.getNetwork(networkName);
    }

    @KafkaListener(topics = "${event-resolver.topic.transfers}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String event, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition, @Header(KafkaHeaders.OFFSET) String offset,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName) {

        try {
            log.info("Partition {}, Offset {}, {} event received {}", partition, offset, topicName, event);

            switch (topicName) {
                case "TRANSFERS":
                    sendTransferToBlockchain(event);
                    break;
                case "LOAN":
                    sendLoanToBlockchain(event);
            }

            log.debug(network.toString());

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error to read transfer event", e);
        }
    }

    private void sendTransferToBlockchain(String transferJSON) {
        try {
            Contract contract = network.getContract("event-chaincode");
            contract.submitTransaction("CreateTransfer", transferJSON);

        } catch (ContractException | InterruptedException | TimeoutException e) {
            log.error("Was not possible to send event to blockchain", e);
        }
    }

    private void sendLoanToBlockchain(String transferJSON) {

    }
}
