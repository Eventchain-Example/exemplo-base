package poc.eventchain.accountservice.web.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionRequest implements Serializable {

    @NotEmpty(message = "Please inform the taxId")
    @Pattern(regexp = "^\\d{3}\\d{3}\\d{3}\\d{2}$", message = "taxId invalid")
    private String taxId;

    @NotEmpty(message = "Please inform the receipt number")
    private String receiptNumber;

    @NotNull(message = "Please inform the amount")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;
}
