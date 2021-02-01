package osmaha.cashmachine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import osmaha.cashmachine.model.Role;
import osmaha.cashmachine.model.RoleE;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleE name);
}
