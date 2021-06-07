package poc.eventchain.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = { "poc.eventchain" })
@ConfigurationPropertiesScan
public class EventResolverServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventResolverServiceApplication.class, args);
	}

}
