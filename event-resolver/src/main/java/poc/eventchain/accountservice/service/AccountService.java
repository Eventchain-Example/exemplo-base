package poc.eventchain.accountservice.service;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.ExecuteStatementResult;
import io.confluent.ksql.api.client.StreamedQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import poc.eventchain.accountservice.model.Account;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AccountService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("account")
    private String topicName;

    public static String KSQLDB_SERVER_HOST = "192.168.1.102";
    public static int KSQLDB_SERVER_HOST_PORT = 8088;

    public void consumeKsql() {
        try {
            ClientOptions options = ClientOptions.create()
                    .setHost(KSQLDB_SERVER_HOST)
                    .setPort(KSQLDB_SERVER_HOST_PORT);
            Client client = Client.create(options);

            // Send requests with the client by following the other example
            Map<String, Object> properties = Collections.singletonMap("auto.offset.reset", "latest");
            client.streamQuery("SELECT * FROM accounts_p EMIT CHANGES;", properties)
                    .thenAccept(streamedQueryResult -> {
                        System.out.println("Query has started. Query ID: " + streamedQueryResult.queryID());

                        RowSubscriber subscriber = new RowSubscriber();
                        streamedQueryResult.subscribe(subscriber);
                    }).exceptionally(e -> {
                        e.printStackTrace();
                        System.out.println("Request failed: " + e);
                return null;
            });

            // Terminate any open connections and close the client
            //client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(Account message) {
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }
}
