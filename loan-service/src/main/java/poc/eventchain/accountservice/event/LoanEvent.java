package poc.eventchain.accountservice.event;

import lombok.*;
import lombok.experimental.SuperBuilder;
import poc.eventchain.accountservice.model.LoanStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoanEvent extends GenericEvent implements Serializable {
    private Integer contractNumber;
    private LoanStatus eventType;
    private Integer version;
    private String taxId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private Integer dueDate;
    private Boolean automaticDebit;
}
