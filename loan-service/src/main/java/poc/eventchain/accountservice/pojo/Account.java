package poc.eventchain.accountservice.pojo;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

	private Long id;
	private String number;
	private String branch;
	private String bank;
	private BigDecimal balance;
	private String taxId;

}
