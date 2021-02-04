package pl.szadejko.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szadejko.api.models.Currency;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
