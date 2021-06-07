package poc.eventchain.accountservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountEvent extends GenericEvent implements Serializable {

    private String number;
    private String branch;
    private String bank;
    private BigDecimal balance;
    private String taxId;
    private Long version;
    private AccountEventType eventType;
}
