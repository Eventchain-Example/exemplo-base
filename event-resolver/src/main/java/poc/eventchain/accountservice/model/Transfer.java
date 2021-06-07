package poc.eventchain.accountservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @JsonProperty("FROM_NUMBER")
    private String fromNumber;

    @JsonProperty("FROM_BRANCH")
    private String fromBranch;

    @JsonProperty("FROM_BANK")
    private String fromBank;

    @JsonProperty("FROM_TAXID")
    private String fromTaxId;

    @JsonProperty("TO_NUMBER")
    private String toNumber;

    @JsonProperty("TO_BRANCH")
    private String toBranch;

    @JsonProperty("TO_BANK")
    private String toBank;

    @JsonProperty("TO_TAXID")
    private String toTaxId;

    @JsonProperty("RECEIPTNUMBER")
    private String receiptNumber;

    @JsonProperty("AMOUNT")
    private BigDecimal amount;
}
