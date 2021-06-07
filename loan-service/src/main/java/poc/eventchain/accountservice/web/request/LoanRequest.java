package poc.eventchain.accountservice.web.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest implements Serializable {

    @NotNull(message = "Please inform the contract number")
    @Min(value = 1, message = "Contract number not allowed")
    private Integer contractNumber;

    @NotEmpty(message = "Please inform the taxId")
    @Pattern(regexp = "^\\d{3}\\d{3}\\d{3}\\d{2}$", message = "taxId invalid")
    private String taxId;

    @NotNull(message = "Please inform the amount")
    @Max(value = 50000, message = "Amount above allowed")
    @Min(value = 1000, message = "Amount below allowed")
    private BigDecimal amount;

    @NotNull(message = "Please inform the interest rate")
    @Max(value = 10, message = "Interest rate above allowed")
    @Min(value = 1, message = "Interest rate below allowed")
    private BigDecimal interestRate;

    @NotNull(message = "Please inform the number of installments")
    @Max(value = 60, message = "Installment number above allowed")
    @Min(value = 12, message = "Installment number below allowed")
    private Integer numberOfInstallments;

    @NotNull(message = "Please inform the due date")
    @Max(value = 28, message = "Due date above allowed")
    @Min(value = 1, message = "Due date below allowed")
    private Integer dueDate;
}
