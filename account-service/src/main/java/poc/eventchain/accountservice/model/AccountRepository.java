package poc.eventchain.accountservice.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

	List<Account> findByTaxIdOrNumber(String taxId, String number);

	Optional<Account> findByTaxId(String taxId);
}
