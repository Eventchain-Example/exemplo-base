package poc.eventchain.accountservice.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findByContractNumber(Integer contractNumber);
}
