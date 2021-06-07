package poc.eventchain.accountservice.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private Integer contractNumber;

	@NotEmpty
	private String taxtId;

	@NotNull
	private BigDecimal amount;

	@NotNull
	private BigDecimal interestRate;

	@NotNull
	private Integer numberOfInstallments;

	@NotNull
	private Integer dueDate;

	@NotNull
	private LoanStatus status;

	@NotNull
	private Boolean automaticDebit;

	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
	private Set<Installment> installments;
}
