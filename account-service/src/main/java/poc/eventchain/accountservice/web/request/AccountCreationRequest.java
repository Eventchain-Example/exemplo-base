package poc.eventchain.accountservice.web.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequest implements Serializable {

    @NotEmpty(message = "Please inform the number")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Account number should have between 4 and 10 digits")
    private String number;

    @NotEmpty(message = "Please inform the taxId")
    @Pattern(regexp = "^\\d{3}\\d{3}\\d{3}\\d{2}$", message = "taxId invalid")
    private String taxId;
}
