package poc.eventchain.accountservice.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;

@Entity
@Table(name = "account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

	private static final long serialVersionUID = 8689837678541382987L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty
	private String number;

	@NotEmpty
	private String branch;

	@NotEmpty
	private String bank;

	@NotNull
	private BigDecimal balance;

	@NotEmpty
	private String taxId;

	@Version
	private Long version;
}
