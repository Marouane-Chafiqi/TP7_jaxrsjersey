package ma.ens.jaxrsjersey.jaxrsjersey.repositories;

import ma.ens.jaxrsjersey.jaxrsjersey.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {
}
