package poc.eventchain.accountservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoanPaymentEvent extends GenericEvent implements Serializable {
    private PaymentType eventType;
    private Integer version;
    private String taxId;
    private Integer installmentNumber;
    private Integer numberOfInstallments;
    private BigDecimal amount;
    private LocalDate dueDate;
}
