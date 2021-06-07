package poc.eventchain.commons.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secretKey = "f26899f4-3bce-4a78-b7e7-7feb714cb5a7";

    // validity in milliseconds
    private long validityInMs = 3600000; // 1h

}
