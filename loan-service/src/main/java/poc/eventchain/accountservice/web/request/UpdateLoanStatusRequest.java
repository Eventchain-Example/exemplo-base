package poc.eventchain.accountservice.web.request;

import io.swagger.annotations.ApiModel;
import lombok.*;
import poc.eventchain.accountservice.model.LoanStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoanStatusRequest {

    @NotNull(message = "Please inform the contract number")
    private Integer contractNumber;

    @NotNull(message = "Please inform the status")
    private LoanStatus status;
}
