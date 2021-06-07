package poc.eventchain.accountservice.model;

import java.io.Serializable;
import java.math.BigDecimal;

//import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

	private static final long serialVersionUID = 8689837678541382987L;
	Long id;

	private String number;

	private String branch;

	private String bank;

	private BigDecimal balance;

	private String owner;
}
